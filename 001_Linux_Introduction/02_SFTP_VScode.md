# Phần 2: Sử dụng SFTP trên VScode để chỉnh sửa souce code 💚

## Các bước làm
- Mở VScode và vào mục Extension
- Tìm kiếm SFTP và install như anh dưới

<img src="image-2.png" alt="SFTP" style="width:500px; height:auto;"/>

- Mở 1 folder trên VScode
- Bấm ***Ctrl+Shift+p*** và chọn ***SFTP config***

<img src="image-3.png" alt="SFTP config" style="width:500px; height:auto;"/>

- Khi này ta sẽ thấy trên Vscode tự động tạo ra 1 file ***.vscode/sftp.json*** với nội dung như ảnh dưới

<img src="image-4.png" alt="SFTP config 1" style="width:500px; height:auto;"/>

- Khi này ta sẽ thay thế nội dung trên như bên dưới:
```s
{
    "name": "hulatho",
    "host": "192.168.30.113",
    "protocol": "sftp",
    "port": 22,
    "username": "hulatho",
    "remotePath": "/home/hulatho",
    "uploadOnSave": true,
    "openSsh": true,
    "password": "1",
    "useTempFile": false
}
```

- Giải thích các thông số bên trên:
    + ***name***: Tên của máy ảo
    + ***host***: Địa chỉ IP của máy ảo
    + ***username***: Tên user 
    + ***remotePath***: Đường dẫn mình muốn liên kết đến máy ảo
    + ***password***: Mật khẩu của máy ảo

- Sau khi xong bước trên, trên thanh công cụ sẽ hiên thị thêm 1 tab là sftp. Ta chuyển qua tab đó có thể thấy được nội dung của folder ***remotePath***

<img src="image-5.png" alt="SFTP content" style="width:500px; height:auto;"/>

### Lấy code từ máy ảo về máy tính cá nhân
- Chuyển qua tab sftp khi này ta muốn lấy folder nào về thì chỉ việc click chuột phải vào folder đó và click ***download***
<img src="image-6.png" alt="SFTP download" style="width:500px; height:auto;"/>

### Đứa code từ máy tính cá nhân lên máy ảo
- Chuyển qua tab Explorer khi này ta muốn đẩy folder nào lên trên ***remotePath*** của máy ảo thì ta chỉ việc click chuột phải vào folder đó và chọn ***Upload folder***

<img src="image-7.png" alt="SFTP upload" style="width:500px; height:auto;"/><br><br>

✅ Vậy ở bài viết này chúng ta đã biết cách sử dụng VScode để lấy source code về cũng như đẩy source code lên máy ảo và chỉnh sửa code. 💯