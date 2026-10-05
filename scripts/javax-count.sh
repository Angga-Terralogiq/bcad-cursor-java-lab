#!/usr/bin/env bash
# Counts javax references left to migrate to jakarta.
#
# Only the Java EE namespaces that moved to jakarta.* are counted. JDK packages
# that still live under javax (javax.crypto, javax.sql and friends) are counted
# separately, because they must NOT be renamed.
#
# Usage: scripts/javax-count.sh          print the two counts
#        scripts/javax-count.sh --list   also print each matching line
set -euo pipefail
cd "$(dirname "$0")/.."

END='([^A-Za-z0-9_]|$)'
MOVED="javax\.(servlet|persistence|validation|xml\.bind|xml\.ws|xml\.soap|jws|activation|ws\.rs|ejb|inject|enterprise|faces|el|websocket|json|mail|jms|batch|interceptor|decorator|resource|transaction|security\.(enterprise|jacc|auth\.message)|annotation\.(PostConstruct|PreDestroy|Resource|Resources|Priority|ManagedBean|Generated|security|sql))$END"
JDK="javax\.(crypto|net|sql|naming|management|security\.auth|security\.cert|transaction\.xa|xml\.(parsers|transform|stream|xpath|namespace|datatype|catalog|crypto|XMLConstants)|annotation\.processing|lang\.model|tools|script|imageio|print|sound|swing|rmi|smartcardio)$END"
# These two match both lists' prefixes and are counted on the other side.
MOVED_OVERLAP="javax\.security\.auth\.message$END"
JDK_OVERLAP="javax\.transaction\.xa$END"

TARGETS=(pom.xml src)

count() {
  { grep -rIoE "$1" "${TARGETS[@]}" 2>/dev/null || true; } | wc -l | tr -d ' '
}

moved=$(( $(count "$MOVED") - $(count "$JDK_OVERLAP") ))
jdk=$(( $(count "$JDK") - $(count "$MOVED_OVERLAP") ))

if [[ "${1:-}" == "--list" ]]; then
  echo "== to migrate =="
  grep -rInE "$MOVED" "${TARGETS[@]}" | grep -vE "$JDK_OVERLAP" || true
  echo "== JDK, leave as is =="
  grep -rInE "$JDK" "${TARGETS[@]}" | grep -vE "$MOVED_OVERLAP" || true
  echo
fi

echo "javax to migrate:        $moved"
echo "javax JDK (leave as is): $jdk"
