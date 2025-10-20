# Phần 3: Các command line hay dùng và cách làm việc với file 💚

## 1. Install các tool cần thiết
```s
sudo apt install gcc
sudo apt install make
sudo apt install vim
sudo apt install tree
```

## 2. Cách làm việc với file
- Để chỉnh sửa 1 file, chúng ta có thể dùng vim, nano... Ở đây mình sử dụng vim nhé.
    + vim main.c: Tạo file main.c
    + Bấm i, i là insert sau đó có thể edit code.
    + set number: Hiển thị line number.
    + dd: Xóa một dòng, quay lại thì bấm u.
    + Ship g: Đưa con trỏ chuột về cuối file.
    + gg: Đưa con trỏ chuột về đầu file.
    + Để thoát file thì:
        + Bấm ESC
        + ": wq": Chú ý dấu "Hai chấm nhé", wq nghĩa là write và quite.
        + ": q!": Chú ý dấu "Hai chấm nhé", q! thoát mà không lưu.

<img src="image-9.png" alt="hello" style="width:500px; height:auto;"/>        

## 3. Các CommandLine hay dùng
***Gõ 2 đến 3 kí tự rồi bấm TAB là ra***
```s
sudo apt-get install XXX: Install XXX
pwd: Xem đường dẫn hiện tại
tree
    sudo apt-get install tree​
    tree . : Hiển thị cấu trúc cây thư mục hiện tại
    tree –a: hiển thị cả file ẩn
    tree -a -L 1 .
ls: Xem trong thư mục hiện tại có những file gì
    ls –l: Hiển thị dưới dạng list.​
    ls –R: Hiển thị tất cả các tập trong thư mục con.​
    ls –a: Hiển thị các tệp ẩn.​
    ls –al: Hiển thị tất cả các thông tin chi tiết như quyền, kích thước, chủ sở hữu...
cd {Folder}: Đi tới thư mục
exit: Thoát khỏi.​
cd -: Toggle giữa 2 folder.​
touch tho.txt: Tạo file.​
vim tho.c: Mở file tho.c nếu đã tồn tại, nếu chưa tồn tại thì tạo mới file.​
rm –rf tho.c: Xóa file.​
rm –rf tho.c *.o: Xóa file tho.c và tất cả các file .o​
clear hoặc ctrl l: Xóa toàn màn hình.​
du –hs: ví dụ du –hs ThoNV/, kiểm tra dung lượng của folder.​
cat tho.c: xem nhanh file.​
less tho.c: file dài quá dùng mũi tên để lên xuống, thoát bấm q​
find < thư mục cần tìm> –name <tên file cần tìm> : tìm file​
lsblk: Kiểm tra ổ cứng.​
mkdir VanTho: Tạo folder VanTho.​
mkdir tho1 tho2 tho3: Tạo nhiều thư mục một lúc.​
cp: sao chép từu tệp sang một thư mục khác, ví dụ cp tho.img /folder_img​
    cp –r folder1 ./vantho/folder​
mv: di chuyển tệp, ví dụ mv file.txt /home/vantho/document​
    mv name new_name: Đổi tên tệp hoặc thư mục.​
ctrl c: Dừng và kết thức lệnh.​
ctrl z: Tạm dừng lệnh.​
scrot –s a.png: Chụp ảnh màn hình.​
Nếu vô tình bấm ctrl S thì terminal sẽ bị đóng băng khi đó chỉ cần bấm ctrl Q
Ta có thể chạy nhiều dòng lệnh bằng việc ngăn cách bởi dấu “;”, ví dụ lệnh 1; lệnh 2; lệnh 3 hoặc có thể dung && nếu mình muốn lệnh sau chạy khi lệnh trước đã thành công.​
Apt list --installed | grep chrome  : Apt list --installed sẽ xuất ra 1 list danh sách, lệnh grep để tìm từ chrome trong mớ đó​
```

✅ Vậy ở bài viết này chúng ta đã biết cách dùng các commandline để tương tác với 1 file và các comamndline cơ bản hay được dùng. 💯