---
name: javax-to-jakarta
description: Migrate this ledger lab from javax / Spring Boot 2.7 to jakarta / Spring Boot 3.5.
---
# javax → jakarta (Track A)

## When to use
Migrating this service (or a twin) from Java 11 + Boot 2.7 + Tomcat 9 + javax to Java 21 + Boot 3.5 + Tomcat 10.1 + jakarta + JUnit 5.

## Preconditions
- JDK 21 and Maven 3.9 available
- Baseline: run `scripts/javax-count.sh` and note both counts
- Log a no-Cursor time estimate first (see README)

## Steps
0. **Plan mode first** — ask for a migration plan in ordered batches with a check after each batch. Review and approve before executing. Then execute one approved batch at a time.
1. `pom.xml`: parent Boot 3.5, `java.version` 21, JAXB → jakarta artifacts
2. EE namespace swaps across `src/` (`javax.` → `jakarta.` where EE)
3. Spring Security / config API updates for Boot 3
4. Tests → JUnit 5; drop Vintage if unused
5. WAR / Tomcat packaging check; `mvn spring-boot:run` smoke

After every batch: `mvn verify` and `scripts/javax-count.sh`. Stop if red.

## Done when
1. `mvn verify` green on JDK 21
2. `javax to migrate: 0`
3. JDK javax count unchanged from baseline
4. README curl checks still pass
5. API contracts and HMAC signing behaviour unchanged (transfers/statement stay identical)

## Out of scope
Secrets, production repos, Playwright/QA, API contract changes, HMAC behaviour changes, and any change beyond this service.
