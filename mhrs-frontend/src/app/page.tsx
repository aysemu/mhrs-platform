import React from 'react';

export default function Home() {
  return (
    <main style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <header style={{
        padding: '1.25rem 2rem',
        borderBottom: '1px solid var(--border)',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        background: 'rgba(15, 23, 42, 0.8)',
        backdropFilter: 'blur(10px)',
        position: 'sticky',
        top: 0,
        zIndex: 50
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div style={{
            width: '40px',
            height: '40px',
            borderRadius: '10px',
            background: 'var(--primary)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontWeight: 'bold',
            fontSize: '1.2rem',
            boxShadow: '0 4px 12px rgba(201, 42, 42, 0.4)'
          }}>
            +
          </div>
          <div>
            <h1 style={{ fontSize: '1.25rem', fontWeight: 'bold', letterSpacing: '-0.02em' }}>MHRS</h1>
            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Merkezi Hekim Randevu Sistemi</p>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '1rem' }}>
          <button style={{
            padding: '0.6rem 1.2rem',
            borderRadius: '8px',
            border: '1px solid var(--border)',
            background: 'transparent',
            color: 'var(--text-main)',
            cursor: 'pointer',
            fontWeight: 500
          }}>
            Giriş Yap
          </button>
          <button style={{
            padding: '0.6rem 1.2rem',
            borderRadius: '8px',
            border: 'none',
            background: 'var(--primary)',
            color: '#fff',
            cursor: 'pointer',
            fontWeight: 500,
            boxShadow: '0 4px 12px rgba(201, 42, 42, 0.3)'
          }}>
            Üye Ol
          </button>
        </div>
      </header>

      {/* Hero Section */}
      <section style={{
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '4rem 2rem',
        textAlign: 'center',
        maxWidth: '900px',
        margin: '0 auto'
      }}>
        <div style={{
          display: 'inline-block',
          padding: '0.4rem 1rem',
          borderRadius: '20px',
          background: 'rgba(201, 42, 42, 0.15)',
          border: '1px solid rgba(201, 42, 42, 0.3)',
          color: '#ff8787',
          fontSize: '0.85rem',
          marginBottom: '1.5rem'
        }}>
          T.C. Sağlık Bakanlığı Güvencesiyle
        </div>

        <h2 style={{ fontSize: '2.5rem', fontWeight: 800, lineHeight: 1.2, marginBottom: '1.5rem' }}>
          Sağlığınız İçin Hızlı ve Kolay <span style={{ color: '#ff6b6b' }}>Hekim Randevusu</span>
        </h2>

        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem', marginBottom: '2.5rem', maxWidth: '650px' }}>
          Türkiye genelindeki tüm devlet hastanelerinden ve polikliniklerinden dilediğiniz doktoru seçerek saniyeler içinde randevu oluşturun.
        </p>

        {/* Quick Action Cards */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
          gap: '1.5rem',
          width: '100%'
        }}>
          <div style={{
            background: 'var(--card-bg)',
            border: '1px solid var(--border)',
            padding: '1.75rem',
            borderRadius: '16px',
            textAlign: 'left',
            backdropFilter: 'blur(12px)',
            transition: 'transform 0.2s ease',
            cursor: 'pointer'
          }}>
            <div style={{ fontSize: '1.5rem', marginBottom: '0.75rem' }}>🏥</div>
            <h3 style={{ fontSize: '1.1rem', marginBottom: '0.5rem' }}>Hastane Randevusu</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>İl, ilçe ve poliklinik seçerek uygun hekimlerden randevu alın.</p>
          </div>

          <div style={{
            background: 'var(--card-bg)',
            border: '1px solid var(--border)',
            padding: '1.75rem',
            borderRadius: '16px',
            textAlign: 'left',
            backdropFilter: 'blur(12px)',
            transition: 'transform 0.2s ease',
            cursor: 'pointer'
          }}>
            <div style={{ fontSize: '1.5rem', marginBottom: '0.75rem' }}>👨‍⚕️</div>
            <h3 style={{ fontSize: '1.1rem', marginBottom: '0.5rem' }}>Aile Hekimi Randevusu</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Bağlı olduğunuz Aile Sağlığı Merkezinden direkt randevu oluşturun.</p>
          </div>

          <div style={{
            background: 'var(--card-bg)',
            border: '1px solid var(--border)',
            padding: '1.75rem',
            borderRadius: '16px',
            textAlign: 'left',
            backdropFilter: 'blur(12px)',
            transition: 'transform 0.2s ease',
            cursor: 'pointer'
          }}>
            <div style={{ fontSize: '1.5rem', marginBottom: '0.75rem' }}>📋</div>
            <h3 style={{ fontSize: '1.1rem', marginBottom: '0.5rem' }}>Randevularım</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Geçmiş ve aktif randevularınızı görüntüleyin veya iptal edin.</p>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer style={{
        padding: '1.5rem 2rem',
        borderTop: '1px solid var(--border)',
        textAlign: 'center',
        color: 'var(--text-muted)',
        fontSize: '0.85rem'
      }}>
        © 2026 MHRS - T.C. Sağlık Bakanlığı. Tüm hakları saklıdır.
      </footer>
    </main>
  );
}
