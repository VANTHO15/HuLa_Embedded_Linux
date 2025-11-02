# 💚 Linux Kernel Module Basic 💛

## 👉 Introduction and Summary

### 1️⃣ Introduction

+ Ở bài trước chúng ta đã biết về lý thuyết Docker và tạo 1 container để chạy được ubuntu 18.04. Nếu các bạn chưa đọc thì xem link này nha [022_Docker.md](../022_Docker/022_Docker.md). Ở bài này chúng ta sẽ tìm hiểu về linux kernel module nhé. Ở bài này sẽ chưa cần đụng đến mạch đâu nhé.

### 2️⃣ Summary

Nội dung của bài viết gồm có những phần sau nhé 📢📢📢:
- [I. Introduction and Summary](#👉-introduction-and-summary)

    - [1. Introduction](#1️⃣-introduction)
    - [2. Summary](#2️⃣-summary)
- [II. Contents](#👉-contents)
    - [1. Giới thiệu](#1️⃣-giới-thiệu)
    - [2. Install docker](#2️⃣-install-docker)
    - [3. Tạo docker file](#3️⃣-tạo-docker-file)
    - [4. Chạy các command](#4️⃣-chạy-các-command)
- [III. Conclusion](#✔️-conclusion)
- [IV. Exercise](#💯-exercise)
- [V. NOTE](#📺-note)
- [VI. Reference](#📌-reference)

## 👉 Contents

​<p align="center">
  <img src="Images/Screenshot_1.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

### 1️⃣ Linux kernel header
+ Như cái tên gọi của nó, kernel header sẽ là các header file ở kernel (.h) để các module include vào và gọi các chức năng ra.
+ Là thành phần được sử dụng để compile cho module của kernel.
+ Kernel header được cài đặt phải trùng với kernel version mà bạn muốn sử dụng (uname –r).
+ Để kiểm tra kernel version ta sẽ gõ lệnh:
```s
uname -r
```

​<p align="center">
  <img src="Images/Screenshot_2.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ Để install kernel header ta chạy câu lệnh dưới
```s
sudo apt install -y linux-headers-`uname -r`
```

+ Khi này kernel header của ta sẽ nằm trong thư mục: /lib/module

​<p align="center">
  <img src="Images/Screenshot_3.png" alt="hello" style="width:1000px; height:auto;"/>   
</p>





## ✔️ Conclusion
Ở bài này chúng ta đã biết các kiến thức về docker và thực hành xung quanh docker. Tiếp theo chúng ta cùng đi tìm hiểu lý thuyết về linux kernel nhé.

## 💯 Exercise
+ Thực hành lại theo bài viết

## 📺 NOTE
+ N/A

## 📌 Reference

[1] Building Embedded Linux Systems.pdf

[2] Linux Device Drivers.pdf

[3] linux-kernel-intro.pdf bootlin

[4] Understanding the LINUX KERNEL.pdf

[5] Linux Device Drivers - ldd3.pdf

[6] Professional Linux Kernel Development 3rd.pdf
