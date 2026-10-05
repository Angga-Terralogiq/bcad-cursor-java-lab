# Ledger service: migration lab

A small banking service you will migrate with Cursor. It keeps customer accounts, moves money between them and exports an account statement as XML. Every transfer is signed with HMAC-SHA256 so the downstream clearing system can check it.

| Endpoint | Who | What it does |
|---|---|---|
| `GET /status` | anyone | Load balancer check, reports whether the database answers |
| `GET /api/accounts` | teller, auditor | Lists accounts |
| `GET /api/accounts/{number}` | teller, auditor | One account and its balance |
| `POST /api/transfers` | teller | Moves money and returns the signed transfer |
| `GET /api/accounts/{number}/statement` | teller, auditor | XML statement for one account |

Lab users (basic auth): `teller` / `teller123` and `auditor` / `auditor123`. The database is H2 in memory, seeded with three accounts on every start.

## Current stack

Java 11, Spring Boot 2.7, Tomcat 9, Hibernate 5, Spring Security 5, JUnit 4. The service builds as a WAR for an external Tomcat and also runs on its own.

## Build, run, test

You need JDK 11 and Maven 3.9.

```bash
mvn verify                  # compile, run all tests, build target/ledger-service.war
mvn spring-boot:run         # start on http://localhost:8080

curl -u auditor:auditor123 http://localhost:8080/api/accounts
curl -u teller:teller123 -H 'Content-Type: application/json' \
  -d '{"fromAccount":"0123456789","toAccount":"9876543210","amount":250000,"reference":"INV-2026-001"}' \
  http://localhost:8080/api/transfers
curl -u auditor:auditor123 http://localhost:8080/api/accounts/9876543210/statement

scripts/javax-count.sh      # javax references still to migrate
```

## Lab goal

Move the service to the target stack:

- Java 21
- Spring Boot 3.5
- Tomcat 10.1, embedded, and the WAR still deploys to an external Tomcat
- Jakarta EE namespaces (`jakarta.*`) wherever they apply
- Tests on JUnit 5

You are done when:

1. `mvn verify` passes on JDK 21 with every existing test still asserting the same behaviour.
2. `scripts/javax-count.sh` shows `javax to migrate: 0`.
3. The `javax JDK (leave as is)` count is the same as when you started.
4. `mvn spring-boot:run` starts and the curl commands above give the same answers.

## Before you open Cursor

Write down how long this migration would take you by hand, without Cursor.

My no-Cursor estimate: ________ hours

## Suggested Cursor workflow

1. Open the repo in Cursor and switch the agent to **Plan mode**. Ask for a migration plan in ordered batches, with a check to run after each batch.
2. Read the plan. Push back on anything that looks wrong or risky, then approve it.
3. Execute one batch at a time. Review the diff for each batch before you accept it.
4. Run `mvn verify` and `scripts/javax-count.sh` after every batch.
5. If a batch goes wrong, restore the checkpoint from before it and retry with a better prompt. Rolling back is cheaper than repairing.
6. Save the final plan. It is the start of a reusable plan for your own services.

Rules in `.cursor/rules/` are shared guardrails (the Jakarta rule applies when Java or build files are in scope; the no-secrets rule always applies). The Track A skill at `.cursor/skills/javax-to-jakarta/SKILL.md` is the migration procedure you invoke. In the live demo, change one line in the Jakarta rule, then re-run the same prompt.
