import './globals.css';
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'MHRS - T.C. Sağlık Bakanlığı Merkezi Hekim Randevu Sistemi',
  description: 'Türkiye Cumhuriyeti Sağlık Bakanlığı Merkezi Hekim Randevu Sistemi Portalı',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="tr">
      <body>{children}</body>
    </html>
  );
}
