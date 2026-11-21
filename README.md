# PlatformerGame

Game đi cảnh 2D viết bằng Java Swing. Di chuyển, nhảy qua địa hình, tránh nước và thu thập toàn bộ xu để chiến thắng.

## Yêu cầu

- **JDK 21** (bao gồm cả `java` và `javac`).
- **Git** nếu tải dự án bằng lệnh clone; có thể tải ZIP thay thế.
- Máy tính có giao diện đồ họa để hiển thị cửa sổ game.

Dự án sử dụng thư viện có sẵn của Java, không cần Maven, Gradle hoặc cài thư viện bên ngoài.

## Cài đặt

1. Cài JDK 21 và thêm thư mục `bin` của JDK vào biến môi trường `PATH`.
2. Mở terminal mới và kiểm tra:

   ```powershell
   java -version
   javac -version
   ```

   Cả hai lệnh cần hiển thị phiên bản 21.

3. Tải mã nguồn và mở thư mục dự án:

   ```powershell
   git clone https://github.com/phoos3110/platformerGame.git
   cd platformerGame
   ```

   Nếu tải ZIP từ GitHub, giải nén rồi mở terminal tại thư mục chứa `src`, `assets` và `RunGame.bat`.

## Biên dịch và chạy trên Windows

Các lệnh sau dùng **PowerShell**, bao gồm terminal PowerShell trong VS Code. Chạy tại thư mục gốc dự án:

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem src -Recurse -Filter *.java).FullName
```

Sau khi biên dịch thành công, mở game:

```powershell
java -cp "bin;assets" run.GameMain
```

Cửa sổ **Platform Game** sẽ xuất hiện. Bấm nút bắt đầu để chơi.

Sau lần biên dịch đầu tiên, bạn cũng có thể nhấp đúp `RunGame.bat` hoặc chạy:

```powershell
.\RunGame.bat
```

**Lưu ý:** `RunGame.bat` chỉ chạy game, không biên dịch. Thư mục `bin` không được lưu trên GitHub, nên phải biên dịch sau khi tải dự án và mỗi khi thay đổi mã Java.

## Biên dịch và chạy trên macOS / Linux

Mở terminal tại thư mục gốc dự án:

```bash
mkdir -p bin
find src -name '*.java' > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
java -cp "bin:assets" run.GameMain
```

Có thể xóa `sources.txt` sau khi biên dịch. Trên macOS / Linux, classpath dùng dấu `:`; trên Windows dùng dấu `;`.

## Cách chơi

| Thao tác | Điều khiển |
| --- | --- |
| Di chuyển sang trái / phải | Phím `←` / `→` |
| Nhảy | Phím `↑` |
| Nhảy lần hai trên không | Nhấn lại `↑` trong vòng 0,5 giây sau lần nhảy đầu |
| Chơi lại sau khi thua hoặc thắng | Phím `R` |
| Về menu sau khi thua hoặc thắng | Phím `Esc` |
| Bật / tắt nhạc và hiệu ứng âm thanh | Vào Options từ menu |
| Thoát game | Nút thoát ở menu hoặc đóng cửa sổ |

Thu thập toàn bộ xu để thắng. Chạm nước sẽ khiến nhân vật chết. Số xu đã thu thập hiển thị ở góc trên bên trái.

## Kiểm tra tự động

Sau khi biên dịch game, chạy trong PowerShell:

```powershell
javac -encoding UTF-8 -cp "bin;assets" -d bin tests/GameSmokeCheck.java
java '-Djava.awt.headless=true' -cp "bin;assets" GameSmokeCheck
```

Bài kiểm tra xác minh vòng đời timer, 100 lần chơi lại, vị trí xu không trùng, dữ liệu bản đồ của nhân vật, bộ nhớ đệm ảnh và xử lý clip âm thanh bị thiếu. Kết quả thành công hiển thị các dòng `PASS`. Đây không thay thế việc chơi thử trên giao diện.

## Xử lý lỗi thường gặp

- **Không tìm thấy `java` hoặc `javac`:** kiểm tra đã cài JDK 21, cấu hình `PATH` và mở lại terminal.
- **`Could not find or load main class run.GameMain`:** biên dịch lại và chạy tại thư mục gốc dự án, kiểm tra có `bin/run/GameMain.class`.
- **`UnsupportedClassVersionError`:** bản Java đang chạy cũ hơn bản dùng để biên dịch. Kiểm tra lại `java -version` và `javac -version`.
- **Lỗi thiếu ảnh hoặc bản đồ:** giữ nguyên cấu trúc `assets` và đưa `assets` vào classpath như lệnh phía trên.
- **Không nghe âm thanh:** kiểm tra âm lượng hệ thống và Options. Nếu thiết bị hoặc clip âm thanh không khả dụng, game vẫn có thể chạy và ghi thông báo lỗi trong terminal.
- **Không điều khiển được nhân vật:** bấm bắt đầu và chọn cửa sổ game để nhận bàn phím.

## Cấu trúc dự án

```text
src/          Mã Java: nhân vật, bản đồ, giao diện và âm thanh
assets/       Hình ảnh, âm thanh và bản đồ TMX/TSX
tests/        Kiểm tra tự động
bin/          File biên dịch, được tạo khi chạy javac
RunGame.bat   Chạy game trên Windows sau khi biên dịch
```
