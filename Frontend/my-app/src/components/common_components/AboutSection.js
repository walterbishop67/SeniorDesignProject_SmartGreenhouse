import React from 'react';
import './AboutSection.css';

const AboutSection = () => {
  return (
    <div className="settings-content">
      <h2>About</h2>
      <div className="settings-section">
        <div className="about-header">
          <h3>Akıllı Sera Sistemi</h3>
          <p className="about-intro">
            Modern tarımı desteklemek ve sera yönetimini dijitalleştirerek çiftçilere zaman kazandırmak amacıyla geliştirilmiş bütünleşik bir platformdur. Projemiz, geleneksel sera yönetiminin zorluklarını azaltmak ve verimi artırmak için yapay zeka, IoT (Nesnelerin İnterneti) ve mobil teknolojileri bir araya getirerek yenilikçi bir çözüm sunar.
          </p>
        </div>
        
        <div className="about-features">
          <h3>Sistem Özellikleri</h3>
          <ul className="feature-list">
            <li><span className="feature-highlight">Sıcaklık ve nem değerleri</span> anlık olarak izlenebilir</li>
            <li><span className="feature-highlight">Yapay zeka destekli bitki hastalık tespiti</span> yapılabilir</li>
            <li><span className="feature-highlight">Otomatik bildirim sistemi</span> sayesinde olumsuz durumlar anında fark edilir</li>
            <li><span className="feature-highlight">Pazar fiyat listeleri</span> günlük olarak görüntülenebilir</li>
            <li><span className="feature-highlight">Takvim ve görev planlayıcı</span> ile seradaki işlemler kolayca takip edilebilir</li>
            <li>İsteğe bağlı olarak <span className="feature-highlight">kamera ile görsel takip</span> yapılabilir</li>
            <li>Uygulama içi <span className="feature-highlight">çiftçi forumu</span> ile bilgi alışverişi sağlanabilir</li>
          </ul>
        </div>
        
        <div className="about-platforms">
          <p>
            Mobil ve web platformları üzerinden erişilebilen bu sistem, yalnızca ölçüm yapan bir uygulama olmanın ötesine geçerek, çiftçilere karar destek mekanizması sunar. Aynı zamanda kullanıcı dostu arayüzü ile hem küçük ölçekli üreticilere hem de profesyonel sera yöneticilerine hitap eder.
          </p>
        </div>
        
        <div className="about-mission">
          <div className="mission-item">
            <h4>Vizyonumuz</h4>
            <p>Sürdürülebilir tarım için teknolojiyi herkesin erişebileceği şekilde sunmak</p>
          </div>
          <div className="mission-item">
            <h4>Misyonumuz</h4>
            <p>Çiftçilerin işlerini kolaylaştırmak ve daha sağlıklı, verimli üretim yapmalarını sağlamaktır</p>
          </div>
        </div>
        
        <div className="about-version">
          <h4>Versiyon</h4>
          <p>v1.2.3</p>
          <p className="release-date">Son Güncelleme: 15 Nisan 2025</p>
        </div>
      </div>
    </div>
  );
};

export default AboutSection; 