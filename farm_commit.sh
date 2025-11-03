#!/usr/bin/env bash
# Farm commit: commit từng file với message & delay ngẫu nhiên (không push)

set -euo pipefail

# === Cấu hình nhanh (có thể override bằng biến môi trường) ===
MIN_DELAY="${MIN_DELAY:-2}"   # giây
MAX_DELAY="${MAX_DELAY:-7}"   # giây

# Danh sách message mẫu (có %s để chèn tên file)
MESSAGES=(
  "chore: update %s"
  "chore: touch %s"
  "docs: refresh %s"
  "style: reformat %s"
  "refactor: clean %s"
  "fix: minor tweak in %s"
  "feat: improve %s"
  "build: update %s"
  "test: adjust %s"
  "perf: optimize %s"
)

# Emoji (tùy thích, có thể để trống)
EMOJIS=("✨" "🛠️" "📄" "🔧" "♻️" "✅" "📝" "🚀" "🔨" "💡")

# === Kiểm tra repo ===
if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "❌ Không ở trong git repo."
  exit 1
fi

# === Lấy danh sách file thay đổi (modified + untracked, bỏ qua ignored/deleted) ===
# -m: modified, -o: others (untracked), --exclude-standard: tôn trọng .gitignore
mapfile -t FILES < <(git ls-files -m -o --exclude-standard)

if [[ ${#FILES[@]} -eq 0 ]]; then
  echo "ℹ️ Không có file thay đổi để commit."
  exit 0
fi

echo "🔍 Tìm thấy ${#FILES[@]} file thay đổi. Bắt đầu farm commit…"
echo "⏱️ Delay ngẫu nhiên từ ${MIN_DELAY}s đến ${MAX_DELAY}s (đổi bằng MIN_DELAY/MAX_DELAY)."

# === Vòng lặp commit từng file ===
for f in "${FILES[@]}"; do
  if [[ ! -e "$f" ]]; then
    # Bỏ qua nếu file đã bị xóa (ls-files thường không liệt kê, nhưng check cho chắc)
    echo "↪️ Bỏ qua (không tồn tại): $f"
    continue
  fi

  # Chọn message & emoji ngẫu nhiên
  msg_tmpl="${MESSAGES[$RANDOM % ${#MESSAGES[@]}]}"
  emoji="${EMOJIS[$RANDOM % ${#EMOJIS[@]}]}"
  # Tên hiển thị (basename để gọn gàng)
  base="$(basename "$f")"
  msg="$(printf "$msg_tmpl" "$base")"
  [[ -n "$emoji" ]] && msg="$emoji $msg"

  # Add + commit riêng từng file
  git add -- "$f"
  if git diff --cached --quiet -- "$f"; then
    echo "⚠️  Không có thay đổi staged cho: $f (bỏ qua)"
    continue
  fi

  git commit -m "$msg" -- "$f"
  echo "✅ Commit: $msg"

  # Delay ngẫu nhiên
  if (( MAX_DELAY > 0 )); then
    # Tính delay trong [MIN_DELAY, MAX_DELAY]
    span=$(( MAX_DELAY - MIN_DELAY + 1 ))
    delay=$(( (span > 0 ? RANDOM % span : 0) + MIN_DELAY ))
    # Chỉ sleep nếu delay > 0
    if (( delay > 0 )); then
      echo "⏳ Nghỉ ${delay}s…"
      sleep "$delay"
    fi
  fi
done

echo "🎉 Xong! Đã farm commit cho từng file. (Không push lên remote.)"
echo "👉 Muốn push thì tự chạy: git push origin <branch-của-bạn>"
