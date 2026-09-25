USE mioto_final_project;

-- -----------------------------------------------------
-- 2. MASTER DATA: Bảng Địa lý (Provinces, Districts)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Provinces (
  id            INT          NOT NULL AUTO_INCREMENT,
  nameProvince  VARCHAR(100) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Districts (
  id            INT          NOT NULL AUTO_INCREMENT,
  nameDistrict  VARCHAR(100) NOT NULL,
  provinceId    INT          NOT NULL,
  PRIMARY KEY (id),
  KEY idx_districts_province (provinceId),
  CONSTRAINT fk_districts_provinces FOREIGN KEY (provinceId) REFERENCES Provinces(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 3. MASTER DATA: Hãng xe, Tính năng & Phí nền tảng
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS CarBrands (
  id            INT          NOT NULL AUTO_INCREMENT,
  nameBrand     VARCHAR(100) NOT NULL,
  createdAt     BIGINT       NOT NULL DEFAULT 0,
  updatedAt     BIGINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Features (
  id            INT          NOT NULL AUTO_INCREMENT,
  nameFeature   VARCHAR(100) NOT NULL,
  createdAt     BIGINT       NOT NULL DEFAULT 0,
  updatedAt     BIGINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS FeePolicies (
  feePolicyId   INT            NOT NULL AUTO_INCREMENT,
  nameFeePolicy VARCHAR(100)   NOT NULL,
  percentFee    DECIMAL(5, 2)  NOT NULL DEFAULT 0.00, -- Ví dụ: 10.00 (%)
  isActive      TINYINT(1)     NOT NULL DEFAULT 1,
  PRIMARY KEY (feePolicyId)
) ENGINE=InnoDB;


-- -----------------------------------------------------
-- 4. BẢNG XE (Cars) & BẢNG LIÊN QUAN
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Cars (
  carId           INT            NOT NULL AUTO_INCREMENT,
  carName         VARCHAR(150)   NOT NULL,
  productionYear  INT            NOT NULL,
  carBrandId      INT            NOT NULL,
  numSeats        TINYINT        NOT NULL DEFAULT 4,
  transmission    TINYINT        NOT NULL DEFAULT 1, -- 1: Tự động, 2: Số sàn
  typeFuel        TINYINT        NOT NULL DEFAULT 1, -- 1: Xăng, 2: Dầu, 3: Điện
  fuelConsumption DECIMAL(4, 1)  NOT NULL DEFAULT 0.0, -- Lít/100km
  description     TEXT,
  pricePerDay     DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
  policy          TEXT,                             -- Điều khoản thuê xe của chủ xe
  districtId      INT            NOT NULL,
  userId          INT            NOT NULL,          -- Chủ xe (Owner)
  status          TINYINT        NOT NULL DEFAULT 1, -- 1: Active, 0: Pending, 2: Blocked
  createdAt       BIGINT         NOT NULL DEFAULT 0,
  updatedAt       BIGINT         NOT NULL DEFAULT 0,
  PRIMARY KEY (carId),
  KEY idx_cars_search (districtId, status, pricePerDay),
  KEY idx_cars_user (userId),
  CONSTRAINT fk_cars_brands FOREIGN KEY (carBrandId) REFERENCES CarBrands(id),
  CONSTRAINT fk_cars_districts FOREIGN KEY (districtId) REFERENCES Districts(id),
  CONSTRAINT fk_cars_users FOREIGN KEY (userId) REFERENCES Users(userId)
) ENGINE=InnoDB;

-- Bảng trung gian Xe - Tính năng (N - N)
CREATE TABLE IF NOT EXISTS CarFeatures (
  id            INT          NOT NULL AUTO_INCREMENT,
  carId         INT          NOT NULL,
  featureId     INT          NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_car_feature (carId, featureId),
  CONSTRAINT fk_carfeatures_cars FOREIGN KEY (carId) REFERENCES Cars(carId) ON DELETE CASCADE,
  CONSTRAINT fk_carfeatures_features FOREIGN KEY (featureId) REFERENCES Features(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Bảng Ảnh xe
CREATE TABLE IF NOT EXISTS CarImages (
  id            INT          NOT NULL AUTO_INCREMENT,
  carId         INT          NOT NULL,
  imageUrl      VARCHAR(255) NOT NULL,
  publicId      VARCHAR(100) NOT NULL DEFAULT '', -- Quản lý trên Cloudinary/S3
  createdAt     BIGINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_carimages_car (carId),
  CONSTRAINT fk_carimages_cars FOREIGN KEY (carId) REFERENCES Cars(carId) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Bảng Lịch chủ xe tự khóa (Unavailabilities)
CREATE TABLE IF NOT EXISTS CarUnavails (
  id            INT          NOT NULL AUTO_INCREMENT,
  carId         INT          NOT NULL,
  startTime     BIGINT       NOT NULL, -- Unix Milliseconds
  endTime       BIGINT       NOT NULL, -- Unix Milliseconds
  createdAt     BIGINT       NOT NULL DEFAULT 0,
  updatedAt     BIGINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_carunavails_overlap (carId, startTime, endTime),
  CONSTRAINT fk_carunavails_cars FOREIGN KEY (carId) REFERENCES Cars(carId) ON DELETE CASCADE
) ENGINE=InnoDB;


-- -----------------------------------------------------
-- 5. BẢNG MÃ GIẢM GIÁ (Vouchers)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Vouchers (
  id              INT            NOT NULL AUTO_INCREMENT,
  title           VARCHAR(150)   NOT NULL,
  code            VARCHAR(50)    NOT NULL,
  imageUrl        VARCHAR(255)   NOT NULL DEFAULT '',
  publicId        VARCHAR(100)   NOT NULL,
  body            TEXT,
  discountPercent DECIMAL(5, 2)  NOT NULL DEFAULT 0.00, -- % Giảm
  maxDiscount     DECIMAL(15, 2) NOT NULL DEFAULT 0.00, -- Số tiền giảm tối đa
  startDate       BIGINT         NOT NULL DEFAULT 0,
  endDate         BIGINT         NOT NULL DEFAULT 0,
  createdAt       BIGINT         NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_vouchers_code (code)
) ENGINE=InnoDB;


-- -----------------------------------------------------
-- 6. BẢNG ĐẶT XE (Bookings) - CORE TRANSACTION
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Bookings (
  id                 INT         NOT NULL AUTO_INCREMENT,
  carId              INT            NOT NULL,
  userId             INT            NOT NULL, -- Khách thuê (Renter)
  voucherId          INT            NULL,     -- Có thể không dùng voucher
  feePolicyId        INT            NOT NULL, -- Policy phí sàn áp dụng
  startDate          BIGINT         NOT NULL, -- Unix Milliseconds
  endDate            BIGINT         NOT NULL, -- Unix Milliseconds
  pickupLocation     VARCHAR(255)   NOT NULL DEFAULT '',
  totalAmount        DECIMAL(15, 2) NOT NULL DEFAULT 0.00, -- Giá gốc chưa giảm
  amountWithVoucher  DECIMAL(15, 2) NOT NULL DEFAULT 0.00, -- Tổng tiền khách phải trả (đã giảm)
  appFee             DECIMAL(15, 2) NOT NULL DEFAULT 0.00, -- Phí sàn thu
  ownCarAmount       DECIMAL(15, 2) NOT NULL DEFAULT 0.00, -- Tiền thực nhận của chủ xe
  status             TINYINT        NOT NULL DEFAULT 1,    -- 1: Pending, 2: Confirmed, 3: In_Progress, 4: Completed, 5: Cancelled
  createdAt          BIGINT         NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_bookings_overlap (carId, startDate, endDate, status),
  KEY idx_bookings_user (userId),
  CONSTRAINT fk_bookings_cars FOREIGN KEY (carId) REFERENCES Cars(carId),
  CONSTRAINT fk_bookings_users FOREIGN KEY (userId) REFERENCES Users(userId),
  CONSTRAINT fk_bookings_vouchers FOREIGN KEY (voucherId) REFERENCES Vouchers(id),
  CONSTRAINT fk_bookings_feepolicies FOREIGN KEY (feePolicyId) REFERENCES FeePolicies(feePolicyId)
) ENGINE=InnoDB;


-- -----------------------------------------------------
-- 7. BẢNG ĐÁNH GIÁ (FeedBacks)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS FeedBacks (
  id            BIGINT        NOT NULL AUTO_INCREMENT,
  comment       TEXT,
  pointStar     INT           NOT NULL DEFAULT 5.0, -- Ví dụ: 4.5 sao
  senderId      INT           NOT NULL, -- Khách hoặc Chủ xe
  receiverId    INT           NOT NULL, -- Người nhận đánh giá
  createdAt     BIGINT        NOT NULL DEFAULT 0,
  updatedAt     BIGINT        NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_feedbacks_receiver (receiverId),
  CONSTRAINT fk_feedbacks_sender FOREIGN KEY (senderId) REFERENCES Users(userId),
  CONSTRAINT fk_feedbacks_receiver FOREIGN KEY (receiverId) REFERENCES Users(userId)
) ENGINE=InnoDB;
