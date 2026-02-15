# CoreBank

هسته‌ی یک سامانه بانکداری (Core Banking) مبتنی بر Spring Boot برای مدیریت مشتری، حساب، کارت، تراکنش و تسهیلات، به‌همراه احراز هویت OTP + Keycloak و کش Redis.

## قابلیت‌های اصلی

- ثبت مشتری و مدیریت اطلاعات پایه.
- ایجاد حساب بانکی (برای نقش ADMIN).
- عملیات مالی: واریز، برداشت و انتقال وجه.
- صدور کارت بانکی برای حساب.
- ثبت/پرداخت تسهیلات (Loan Request / Repay).
- داشبورد مدیریتی اولیه.
- احراز هویت دو مرحله‌ای (Credentials + OTP) با Keycloak و Redis.
- Endpointهای مانیتورینگ Actuator.

## تکنولوژی‌ها

- Java 21
- Spring Boot (Web, Data JPA, Validation, Security, OAuth2 Resource Server)
- PostgreSQL
- Redis
- Keycloak
- Thymeleaf (فرم ثبت مشتری)
- Maven
- Docker Compose

## ساختار ماژول‌ها

```text
src/main/java/ir/tejaratBank/core/CoreBank
├── controller   # APIها
├── service      # منطق کسب‌وکار
├── data
│   ├── model    # Entityها
│   ├── repository
│   └── dto
├── config       # Security/Redis config
├── exception    # Exceptionهای دامنه
└── aspect       # AOP (مانیتورینگ عملکرد تراکنش)
```

## پیش‌نیازها

- JDK 21
- Maven (یا استفاده از `./mvnw`)
- Docker + Docker Compose

## راه‌اندازی سریع

### 1) بالا آوردن زیرساخت‌ها

```bash
docker compose up -d postgres redis redis-insight keycloak
```

سرویس‌ها روی پورت‌های زیر در دسترس هستند:
- PostgreSQL: `localhost:5433`
- Redis: `localhost:6379`
- Redis Insight: `localhost:5540`
- Keycloak: `localhost:8180`

### 2) اجرای برنامه

```bash
./mvnw spring-boot:run
```

> در صورت نیاز، ابتدا build بگیرید:

```bash
./mvnw clean package -DskipTests
```

### 3) بررسی سلامت سرویس

```bash
curl http://localhost:8080/actuator/health
```

## تنظیمات کلیدی

تنظیمات اصلی در فایل `src/main/resources/application.properties` قرار دارد:

- `spring.datasource.url=jdbc:postgresql://localhost:5433/corebank`
- `spring.data.redis.host=localhost`
- `spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/tejarat-realm`
- `management.endpoints.web.exposure.include=health,info,metrics`

## جریان احراز هویت (Login + OTP)

1. کاربر با `username/password` به `/auth/login` درخواست می‌دهد.
2. اعتبارسنجی اولیه روی Keycloak انجام می‌شود.
3. یک `loginId` موقت ساخته و Token/Session موقت در Redis ذخیره می‌شود.
4. OTP ارسال می‌شود (`/auth/send-otp`).
5. با `/auth/verify-otp` و کد صحیح، access token واقعی برگردانده می‌شود.

## APIهای مهم

### مشتری
- `GET /customers/register` نمایش فرم ثبت‌نام
- `POST /customers/register` ثبت مشتری

### حساب
- `POST /api/accounts/create?customerId={id}&type={SAVING|CURRENT}`
- `GET /api/accounts/my-details`

### تراکنش
- `POST /api/transactions/deposit`
- `POST /api/transactions/withdraw`
- `POST /api/transactions/transfer`
- `GET /api/transactions/{accountId}`

نمونه بدنه تراکنش:

```json
{
  "sourceAccountId": 1,
  "destinationAccountId": 2,
  "amount": 1500000,
  "description": "salary transfer"
}
```

### کارت
- `POST /api/cards/issue?accountId={id}&pin={1234}`

### وام
- `POST /api/loans/request?customerId={id}&amount={value}&installments={count}&interestRate={rate}`
- `POST /api/loans/repay?loanId={id}&sourceAccountId={id}&amount={value}`
- `DELETE /api/loans/{id}`

### ادمین
- `GET /api/admin/dashboard?requesterId={id}`
- `POST /api/admin/promote?customerId={id}`

## نکات توسعه

- مدیریت خطاها در `GlobalExceptionHandler` انجام می‌شود.
- برای توسعه‌ی محلی، `app.security.enabled=false` در properties فعال است.
- اگر قصد توسعه قابلیت‌ها را دارید، فایل `CONTINUATION_PLAN.md` یک نقشه‌راه فازبندی‌شده ارائه می‌دهد.

## مجوز و مشارکت

در حال حاضر لایسنس پروژه مشخص نشده است. برای مشارکت، Issue/PR ثبت کنید و قبل از ارسال تغییرات، build پروژه را اجرا کنید.
