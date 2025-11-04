# 💚 Character Device Driver IOCTL 💛

## 👉 Introduction and Summary

### 1️⃣ Introduction

+ Ở bài trước chúng ta đã tạo ra được character device driver và sử dụng userspace để blynk led. Nếu các bạn chưa đọc thì xem link này nha [027_Character_Device_Blynk_Led.md](../027_Character_Device_Blynk_Led/027_Character_Device_Blynk_Led.md). Ở bài này chúng ta sẽ tìm hiểu về IOCTL trong character device driver và thực .

### 2️⃣ Summary

Nội dung của bài viết gồm có những phần sau nhé 📢📢📢:
- [I. Introduction and Summary](#👉-introduction-and-summary)

    - [1. Introduction](#1️⃣-introduction)
    - [2. Summary](#2️⃣-summary)
- [II. Contents](#👉-contents)
    - [1. IOCTL Device Driver](#1️⃣-ioctl-device-driver)
    - [2. Thực hành](#2️⃣-thực-hành)
- [III. Conclusion](#✔️-conclusion)
- [IV. Exercise](#💯-exercise)
- [V. NOTE](#📺-note)
- [VI. Reference](#📌-reference)

## 👉 Contents

### 1️⃣ IOCTL Device Driver
+ File_operations không đủ khả năng làm API cho nhiều loại thiết bị character và block device.

+ Các thao tác dành riêng cho Device như thay đổi tốc độ serial port, thiết lập âm lượng trên soundcard, cấu hình các tham số liên quan đến video trên framebuffer không được xử lý bởi các file operations.

+ Ioctl (input-output control) là một loại system call dành riêng cho thiết bị. Nó cho phép mở rộng khả năng của driver bằng các thao tác dành riêng cho driver.

+ Ứng dụng chính của việc sử dụng ioctl là trong trường hợp xử lý một số hoạt động cụ thể của device mà không có system call nào phù hợp được cung cấp bởi hệ thống.

+ Ioctl được sử dụng trong cả user space và kernel space.

+ Nếu dùng system call thì dùng 3 cái liền là open read write rồi close, trong khi ioctl thì dùng là đọc xuống các kiểu luôn nên nhanh hơn và xịn sò hơn

+ Using IOCLT in kernel space
	+ long ioctl(struct file *f, unsigned int cmd, unsigned long arg); 
	+ Argument use for cmd. Usually is pointer to a struct of argument

+ Chúng ta cần tìm hiểu các vấn đề sau với IOCTL:
	+ Tạo IOCTL command trong driver
	+ Tạo IOCTL function trong the driver
	+ Tạo  IOCTL command trên userspace application
	+ Sử dụng ioctl() system call trên Userspace

+ Các macro được sử dụng để tạo số lệnh là:
	+ **_IO(int type, int number)**: được sử dụng cho một ioctl đơn giản không gửi gì ngoài loại và số và không nhận lại gì ngoài một giá trị (số nguyên)
	+ **_IOR(int type, int number, data_type)**: được sử dụng cho ioctl đọc dữ liệu từ trình điều khiển thiết bị. Trình điều khiển sẽ được phép trả về byte sizeof(data_type) cho người dùng
	+ **_IOW(int type, int number, data_type)**: tương tự _IOR, nhưng dùng để ghi dữ liệu vào driver
	+ **_IORW(int type, int number, data_type)**: sự kết hợp của _IOR và _IOW. Nghĩa là, dữ liệu vừa được ghi vào trình điều khiển, sau đó được máy khách đọc lại từ trình điều khiển.

	+ Trong đó:
		+ **type** : số nguyên 8 bit được chọn dành riêng cho trình điều khiển thiết bị. nên chọn loại để không xung đột với các trình điều khiển khác có thể đang "nghe" cùng một bộ mô tả tập tin
		+ **number** : số lệnh nguyên 8 bit
		+ **data_type** : tên của loại được sử dụng để tính toán số byte được trao đổi giữa máy khách và trình điều khiển. Ví dụ, là tên của một cấu trúc.

***Using IOCLT in kernel space***
```c
#include <linux/ioctl.h>

struct led_cfg {
	int mode;       
	int blink_period;
};

#define LED_IOC_MAGIC 'k'
#define LED_CLR_CONFIG _IO (LED_IOC_MAGIC, 1)
#define LED_GET_CONFIG _IOR(LED_IOC_MAGIC, 2, struct led_cfg *)
#define LED_SET_CONFIG _IOW(LED_IOC_MAGIC, 3, struct led_cfg *)

static long my_ioctl(struct file *filp,unsigned int cmd,unsigned long arg) 
{    
	switch (cmd) 
	{
		case LED_GET_CONFIG:           
			/* Code for control led */          
			break;
		case LED_CLR_CONFIG:          
			/* Code for control led */             
			break;
		/*...*/    
		default:            
			return -EINVAL;  
	}   
	return 0;
}
static struct file_operations query_fops = {
        .owner = THIS_MODULE,
        .open = my_open,
        .release = my_release,
        .unlocked_ioctl = my_ioctl
};
```

***Using IOCLT in urser space***
```c
#include <sys/ioctl.h>
#include "cmd_define.h" /*use the same define with kernel*/
/*...*/
fd = open(file_name, O_RDWR);
if (ioctl(fd, LED_GET_CONFIG, &led) == -1) 
{               
	perror("led_apps ioctl get");
}
/*...*/
```

***ioclt so với sysfs***
+ sysfs hữu ích trong việc hiển thị các thuộc tính của thiết bị cho không gian người dùng, đặc biệt là cho người dùng trên bảng điều khiển hoặc tập lệnh shell. Truyền dữ liệu dưới dạng một chuỗi văn bản đơn giản.

+ ioctl hữu ích cho việc truyền dữ liệu (không phải chuỗi văn bản đơn giản) giữa không gian người dùng và trình điều khiển, và cần một chương trình C hoặc tương tự để sử dụng. Các ioctl tùy chỉnh phù hợp để viết trình điều khiển trong kernel và đưa logic vào một chương trình không gian người dùng tương ứng.

+ Đối với các thao tác đọc/ghi nhỏ (thay đổi cấu hình, thông tin, v.v.), hãy hiển thị thông qua sysfs.

+ Đối với việc truyền dữ liệu lớn hoặc lệnh cụ thể, hãy sử dụng ioctl.

### 2️⃣ Thực hành

***Thêm IOCTL vào file operation***


## ✔️ Conclusion
Ở bài này chúng ta đã biết cách tạo IOCTL, áp dụng vào kernel driver và điều khiển được led. Tiếp theo chúng ta sẽ đến bài multi device nhé, nghĩa là 1 driver nhưng điều khiển nhiều device.

## 💯 Exercise
+ Thực hành theo bài viết

## 📺 NOTE
+ N/A

## 📌 Reference

[1] i.MX Linux Reference Manual

[2] https://docs.kernel.org/driver-api/ioctl.html

[3] https://man7.org/linux/man-pages/man2/ioctl.2.html
