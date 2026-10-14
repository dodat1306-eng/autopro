# AutoPro

AutoPro connects car buyers and sellers. The website uses Java 17, JSP/Servlet,
MySQL or MariaDB, Apache Tomcat 10.1, and the MVC pattern. Buyers and sellers
arrange payments and contracts outside the platform.

## Features

- Register, log in, recover passwords, and manage account profiles.
- Seller verification with administrator approval and identity documents.
- Publish and moderate listings with car specifications and multiple photos.
- Search and filter cars by brand, price, province, region, and specifications.
- Saved listings, messaging, connection requests, and viewing appointments.
- Buyer and seller transaction confirmations, notifications, and reports.
- Seller and administrator dashboards.
- Priority workflow additions: seller acceptance, appointment rescheduling,
  disputes with evidence, contact verification, and identity review history.

## Project Layout

| Directory | Contents |
| --- | --- |
| `src/java/controller` | Servlet controllers |
| `src/java/dao` | Database access and business transactions |
| `src/java/model` | Domain models |
| `src/java/util` | Shared helpers and notification delivery |
| `src/java/filter` | Encoding, request protection, document access |
| `web` | JSP pages, shared fragments, logos, static assets |
| `database` | Schema, public catalogs, and migration scripts |
| `lib` | Dependencies used by the NetBeans/Ant build |
| `nbproject` | Shared NetBeans project configuration |
| `test` | Integration checks for priority workflows |
| `tools` | Project documentation tools |

## Run In NetBeans

1. Install JDK 17, MySQL/MariaDB, and Apache Tomcat 10.1.
2. Open this directory as an existing NetBeans Java Web project.
3. Register your local Tomcat server and select it in Project Properties.
4. Create a database named `car_market` with charset `utf8mb4`.
5. Import `database/schema.sql`, then `database/catalogs.sql`.
6. Run `database/20260914_priority_workflows.sql` before using priority workflows.
   The other migration files document previous upgrades already included in the
   schema snapshot; do not rerun their index/constraint additions on a fresh import.
7. Configure the database and optional notification settings, then restart Tomcat.
8. Clean and Build, then Run. The application context is `/xetot`.

The committed database files contain structure and public car-brand/province
catalogs. They do not include user accounts, passwords, messages, transactions,
identity documents, or other uploaded content. Register a new account and provision
your own administrator locally.

## Configuration

Database configuration is read from Java system properties or environment variables:

| Variable | Purpose |
| --- | --- |
| `AUTOPRO_DB_URL` | JDBC URL for your local database |
| `AUTOPRO_DB_USER` | Database username |
| `AUTOPRO_DB_PASSWORD` | Database password |
| `AUTOPRO_PRIVATE_UPLOAD_DIR` | Private identity/evidence document directory |

Defaults target `localhost:3306/car_market` with user `root` and an empty password.
Set appropriate credentials for your installation.

For Gmail SMTP and Twilio SMS, use `notification-config.example.bat` as the
configuration reference. Supply your credentials in your local Tomcat environment.
Contact verification requires a working email or SMS provider; an account is not
created until its registration OTP is verified.

Uploaded car photos and avatars are stored locally under `web/uploads`. Private
identity documents and evidence use `AUTOPRO_PRIVATE_UPLOAD_DIR`, defaulting to
`.autopro/private-documents` under the operating-system user directory. Keep that
directory when redeploying.

## Checks

Java sources and JSP pages can be compiled with JDK 17 and Tomcat's Jasper JSP
compiler. `test/PriorityWorkflowTest.java` exercises seller acceptance, concurrent
requests, expiry, appointments, disputes, OTP verification, and identity review.
Run it against a disposable database whose name ends in `_qa`, never your working
database. Configure `AUTOPRO_DB_URL` to point to that database first.

## Repository Contents

The repository keeps application source, required JARs, static branding, NetBeans
configuration, and database scripts. Build output, local server configuration,
uploaded user files, credentials, and generated Word/PDF reports are ignored.
