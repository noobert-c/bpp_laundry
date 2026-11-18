
package main

import (
	"bufio"
	"context"
	"fmt"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"

	_ "modernc.org/sqlite"
	"github.com/mdp/qrterminal/v3"

	"go.mau.fi/whatsmeow"
	"go.mau.fi/whatsmeow/store/sqlstore"
	"go.mau.fi/whatsmeow/types"
	"go.mau.fi/whatsmeow/types/events"
	waLog "go.mau.fi/whatsmeow/util/log"
	waProto "go.mau.fi/whatsmeow/proto/waE2E"

	"google.golang.org/protobuf/proto"
)

var (
	ctx    = context.Background()
	client *whatsmeow.Client
)

func main() {
	// Membuat database dan mengambil sesi perangkat.
	dbLog := waLog.Stdout("Database", "ERROR", true)

	container, err := sqlstore.New(
    	ctx,
    	"sqlite",
    	"file:whatsapp.db?_pragma=foreign_keys(1)",
    	dbLog,
	)
	if err != nil {
		fmt.Println("Database error:", err)
		return
	}

	device, err := container.GetFirstDevice(ctx)
	if err != nil {
		fmt.Println("Device error:", err)
		return
	}

	client = whatsmeow.NewClient(
		device,
		waLog.Stdout("WhatsApp", "WARN", true),
	)

	// Mendaftarkan handler pesan masuk.
	client.AddEventHandler(handleEvent)

	// Login menggunakan sesi tersimpan atau QR.
	if client.Store.ID == nil {
		qrChan, err := client.GetQRChannel(ctx)
		if err != nil {
			fmt.Println("QR error:", err)
			return
		}

		if err := client.Connect(); err != nil {
			fmt.Println("Connect error:", err)
			return
		}

		fmt.Println("Menunggu QR login...")

		for evt := range qrChan {
			switch evt.Event {
			case "code":
				fmt.Println("\nScan QR ini melalui WhatsApp:")
				fmt.Println("Perangkat tertaut > Tautkan perangkat")

				qrterminal.GenerateHalfBlock(
					evt.Code,
					qrterminal.L,
					os.Stdout,
				)

			case "success":
				fmt.Println("QR berhasil dipindai.")

			case "timeout":
				fmt.Println("QR kedaluwarsa. Jalankan ulang program.")

			default:
				fmt.Println("Status:", evt.Event)
			}
		}

		if client.Store.ID == nil {
			fmt.Println("Login belum berhasil.")
			client.Disconnect()
			return
		}
	} else {
		fmt.Println("Sesi ditemukan. Menghubungkan...")
		if err := client.Connect(); err != nil {
			fmt.Println("Connect error:", err)
			return
		}
	}

	fmt.Println("\nWhatsApp terhubung!")
	fmt.Println("Nomor akun:", client.Store.ID.String())
	fmt.Println()
	fmt.Println("Perintah:")
	fmt.Println("  send <nomor> <pesan>")
	fmt.Println("  exit")
	fmt.Println()

	// Menutup koneksi saat Ctrl+C ditekan.
	sig := make(chan os.Signal, 1)
	signal.Notify(sig, os.Interrupt, syscall.SIGTERM)

	go func() {
		<-sig
		fmt.Println("\nMenutup koneksi...")
		client.Disconnect()
		os.Exit(0)
	}()

	runConsole()
	client.Disconnect()
}

// Membaca pesan masuk.
func handleEvent(evt any) {
	switch v := evt.(type) {
	case *events.Message:
		if v.Info.IsFromMe {
			return
		}

		text := getText(v.Message)
		if text == "" {
			return
		}

		fmt.Println()
		fmt.Println("===== PESAN MASUK =====")
		fmt.Println("Dari   :", v.Info.Sender.String())
		fmt.Println("Nama   :", v.Info.PushName)
		fmt.Println("Waktu  :", v.Info.Timestamp.Format(time.RFC3339))
		fmt.Println("Pesan  :", text)
		fmt.Println("=======================")
		fmt.Print("wa> ")
	}
}

// Mengambil teks pesan biasa atau extended text.
func getText(msg *waProto.Message) string {
	if msg == nil {
		return ""
	}

	if text := msg.GetConversation(); text != "" {
		return text
	}

	return msg.GetExtendedTextMessage().GetText()
}

// Mengirim pesan ke nomor WhatsApp.
func sendMessage(number, text string) error {
	number = strings.TrimPrefix(
		strings.TrimSpace(number),
		"+",
	)

	// Menyisakan angka saja.
	var digits strings.Builder
	for _, r := range number {
		if r >= '0' && r <= '9' {
			digits.WriteRune(r)
		}
	}

	number = digits.String()
	if len(number) < 8 {
		return fmt.Errorf("nomor tidak valid")
	}

	jid := types.NewJID(number, types.DefaultUserServer)

	message := &waProto.Message{
		Conversation: proto.String(text),
	}

	resp, err := client.SendMessage(ctx, jid, message)
	if err != nil {
		return err
	}

	fmt.Println("Pesan terkirim. ID:", resp.ID)
	return nil
}

// Console interaktif.
func runConsole() {
	scanner := bufio.NewScanner(os.Stdin)

	for {
		fmt.Print("wa> ")

		if !scanner.Scan() {
			return
		}

		line := strings.TrimSpace(scanner.Text())
		if line == "" {
			continue
		}

		parts := strings.SplitN(line, " ", 3)

		switch strings.ToLower(parts[0]) {
		case "send":
			if len(parts) < 3 {
				fmt.Println("Format: send <nomor> <pesan>")
				continue
			}

			if err := sendMessage(parts[1], parts[2]); err != nil {
				fmt.Println("Gagal mengirim:", err)
			}

		case "exit":
			return

		default:
			fmt.Println("Perintah tidak dikenal.")
			fmt.Println("Gunakan: send <nomor> <pesan> atau exit")
		}
	}
}
