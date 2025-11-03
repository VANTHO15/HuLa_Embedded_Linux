# 💚 Character Device Driver 💛

## 👉 Introduction and Summary

### 1️⃣ Introduction

+ Ở bài trước chúng ta đã blynk led sử dụng kernel module build-in. Nếu các bạn chưa đọc thì xem link này nha [025_Linux_BuildIn_Customization.md](../025_Linux_BuildIn_Customization/025_Linux_BuildIn_Customization.md). Ở bài này chúng ta sẽ tìm hiểu về Character Device Driver trong linux.

### 2️⃣ Summary

Nội dung của bài viết gồm có những phần sau nhé 📢📢📢:
- [I. Introduction and Summary](#👉-introduction-and-summary)

    - [1. Introduction](#1️⃣-introduction)
    - [2. Summary](#2️⃣-summary)
- [II. Contents](#👉-contents)
    - [1. Các bước để tạo driver buildin](#1️⃣-các-bước-để-tạo-driver-buildin)
    - [2. Driver Blynk Led Buildin](#2️⃣-driver-blynk-led-buildin)
- [III. Conclusion](#✔️-conclusion)
- [IV. Exercise](#💯-exercise)
- [V. NOTE](#📺-note)
- [VI. Reference](#📌-reference)

## 👉 Contents

### 1️⃣ Cách tương tác với hardware

+ Để user tương tác được với hardware thì trong kernel ta tạo ra 1 device driver, trong bài này device driver sẽ là character device driver và sau đó device driver sẽ tương tác với hardware tương ứng với driver đó. User sẽ không trực tiếp tương tác được với hardware

+ Device driver thì có nhiều loại trong đó character device driver là 1 trong số đó. Như cái tên của nó Device driver nghĩa là driver để quản lý 1 thiết bị. Khi device driver được load vào OS, nó sẽ hiển thị các giao diện không gian người dùng để ứng dụng người dùng có thể giao tiếp với device. Ví dụ giờ mình có 1 con RTC DS1307, và mình muốn cấu hình nó, về cơ bản thì ta sẽ tạo 1 thiết bị và thực hiện đọc ghi với nó 

​<p align="center">
  <img src="Images/Screenshot_3.png" alt="hello" style="width:900px; height:auto;"/>   
</p>

+ Gọi là character device driver nghĩa là ta sẽ tạo ra 1 "System object" giống như 1 file dưới kernel. Mà File thì ta có thể dùng các API từ user như open, read, write, close...

+ Tất cả device file đều được lưu trong đường dẫn /dev

​<p align="center">
  <img src="Images/Screenshot_2.png" alt="hello" style="width:300px; height:auto;"/>   
</p>

​<p align="center">
  <img src="Images/Screenshot_1.png" alt="hello" style="width:1000px; height:auto;"/>   
</p>

​<p align="center">
  <img src="Images/Screenshot_4.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

### 2️⃣ Tạo character device driver
+ Để tạo 1 Character Device Driver ta thực hiện qua 3 bước:
	1. Allocate device number (major/minor): Tạo ra ID cho Character Device Driver
	2. Create Device File: Tạo Device File và gán với ID
	3. Register File operations: Đăng kí Device File với các API open, read, write...

+ Bây giờ chúng ta đi vào từng bước tạo Device File nhé

***Bước 1: Allocate device number***
+ Device number
	+ Major number: Là số xác định liên kết giữa driver và device. Một major number có thể được chia sẻ giữa nhiều device driver
	+ Minor number: Là số dùng để phân biệt các thiết bị vật lý riêng lẻ
	+ Như điện thoại thì có điện thoại iphone, Samsung, nên để phân biệt thì có major number, nhưng iphone thì có nhiều loại như ip13 13 pro nên sẽ có thêm minor number

+ Để cấp phát device number ta có thể sử dụng một trong hai cách là Static allocating và Dynamic allocating.

+ Static allocating

***Bước 2: Create Device File***

***Bước 3: Register File operations***


## ✔️ Conclusion
Ở bài này chúng ta đã biết cách tạo ra 1 Character Device Driver file. Tiếp theo chúng ta sẽ áp dụng và có thể nháy led nhé.

## 💯 Exercise
+ Thực hành theo bài viết

## 📺 NOTE
+ N/A

## 📌 Reference

[1] i.MX Linux Reference Manual

[2] https://man.cx/ioremap(9)

[3] https://www.kernel.org/doc/html/next/kbuild/kconfig-language.html
