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
    - [3. Thực hành](#3️⃣-thực-hành)
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
  + Static allocating: Ta dùng hàm register_chrdev_region
  + Dynamic allocating: Ta dùng hàm alloc_chrdev_region
    + Cách này hay dùng hơn vì Khi ta cấp phát là Dynamic thì hệ thống sẽ tìm một số phù hợp mà chưa được sử dụng bởi số nào hết

+ Device number được đại diện bởi struct dev_t, bản chất nó cũng là số nguyên thôi

+ alloc_chrdev_region(&mdev.dev_num, 0, 1, "m-cdev")
  + Số 0: là giá trị minor bắt đầu
  + Số 1: là số lượng minor 
  + Ví dụ major là 99 và số lượng minor là 3 thì ta sẽ có 99 và 4 , 99 và 5, 99 và 6

```c
int alloc_chrdev_region(dev_t * dev, unsigned baseminor, unsigned count, const char * name);
  + *dev: truyền vào device number để lấy được device number
  + Baseminor: minor bắt đầu từ số này
  + Count: Số lượng của số minor  được yêu cầu
  + Name: là tên mà mình đặt cho phạm vi số device
```

+ Chú ý bất cứ 1 thằng cấp phát nào cũng có 1 hàm hủy

​<p align="center">
  <img src="Images/Screenshot_5.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

​<p align="center">
  <img src="Images/Screenshot_6.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ File code majorminor.c
```c
#include <linux/module.h> /* Thu vien nay dinh nghia cac macro nhu module_init va module_exit */
#include <linux/fs.h>     /* Thu vien nay dinh nghia cac ham allocate major & minor number */

#define DRIVER_AUTHOR "hulatho hulatho@hula.com.vn"
#define DRIVER_DESC "Hello world kernel module"

struct m_foo_dev
{
    dev_t dev_num; 
} mdev;

static int
    __init
    hello_world_init(void) /* Constructor */
{
    /* 1.1 Dynamic allocating device number (cat /pro/devices) */
    if (alloc_chrdev_region(&mdev.dev_num, 0, 1, "m-cdev") < 0) 
    {
        pr_err("Failed to alloc chrdev region\n");
        return -1;
    }

    /* 1.2 Static allocating device number (cat /pro/devices) */
    /* register_chrdev_region(&mdev.dev_num, 1, "m-cdev") */

    pr_info("Major = %d Minor = %d\n", MAJOR(mdev.dev_num), MINOR(mdev.dev_num));

    pr_info("Hello HuLa\n");
    return 0;
}
static void
    __exit
    hello_world_exit(void) /* Destructor */
{
    /* 1. Unallocating device number */
    unregister_chrdev_region(mdev.dev_num, 1);
    pr_info("Goodbye HuLa\n");
}

module_init(hello_world_init);
module_exit(hello_world_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR(DRIVER_AUTHOR);
MODULE_DESCRIPTION(DRIVER_DESC);
```

+ File code Makefile
```Makefile
EXTRA_CFLAGS = -Wall
obj-m = majorminor.o

KDIR = /lib/modules/`uname -r`/build

all:
	make -C $(KDIR) M=`pwd` modules

clean:
	make -C $(KDIR) M=`pwd` clean
```

+ Giải thích cách chạy. Khi chạy chương trình trên và insmod thì chương trình sẽ in ra số major number, ví dụ ở đây là 240
```bash
$ make all
$ sudo insmod majorminor.ko
$ sudo dmesg | tail
$ vim /proc/devices      : Để kiểm tra major được ghi vào hệ thống, ta thấy số 240 m-cdev
$ ls –l /dev/ | grep 240 : Để kiểm tra số major và tên ta đã đặt, ta thấy 240 m-device
```

​<p align="center">
  <img src="Images/Screenshot_23.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

***Bước 2: Create Device File***
+ Bây giờ ta sẽ tạo device file để liên kết với device number
+ Ta nhìn vào ảnh dưới, ta thấy Device file sẽ nằm trong class device nên trước khi tạo device ta sẽ phải tạo class trước
​<p align="center">
  <img src="Images/Screenshot_7.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ Ta tạo class
  + *Owner: Con trỏ trở tới OWM
  + Name: tên của class này

​<p align="center">
  <img src="Images/Screenshot_10.png" alt="hello" style="width:1000px; height:auto;"/>   
</p>

+ Ta tạo Device
  + Class: pointer trở tới class
  + Parent: pointer trở tới parent struct device của device mới này
  + Devt: device number
  + Drvdata: dữ liệu sẽ được thêm vào thiết bị để gọi lại
  + Fmt: tên của device

​<p align="center">
  <img src="Images/Screenshot_11.png" alt="hello" style="width:1000px; height:auto;"/>   
</p>

+ Xem code 2 bước tạo ở dưới đây

​<p align="center">
  <img src="Images/Screenshot_9.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ Source code tạo Device file và Makefile

+ File devicefile.c
```c
#include <linux/module.h> /* Thu vien nay dinh nghia cac macro nhu module_init/module_exit */
#include <linux/fs.h>     /* Thu vien nay dinh nghia cac ham allocate major/minor number */
#include <linux/device.h> /* Thu vien nay dinh nghia cac ham class_create/device_create */

#define DRIVER_AUTHOR "hulatho hulatho@hula.com.vn"
#define DRIVER_DESC "Hello world kernel module"

struct m_foo_dev
{
    dev_t dev_num;
    struct class *m_class;
} mdev;

static int __init hello_world_init(void) /* Constructor */
{
    /* 1. Allocating device number */
    if (alloc_chrdev_region(&mdev.dev_num, 0, 1, "m-driver") < 0)
    {
        pr_err("Failed to alloc chrdev region\n");
        return -1;
    }
    pr_info("Major = %d Minor = %d\n", MAJOR(mdev.dev_num), MINOR(mdev.dev_num));

    /* 02. Creating struct class */
    if ((mdev.m_class = class_create(THIS_MODULE, "m_class")) == NULL)
    {
        pr_err("Cannot create the struct class for my device\n");
        goto rm_device_numb;
    }

    /* 03. Creating device*/
    if ((device_create(mdev.m_class, NULL, mdev.dev_num, NULL, "m_device")) == NULL)
    {
        pr_err("Cannot create my device\n");
        goto rm_class;
    }

    pr_info("Hello world kernel module\n");
    return 0;

rm_class:
    class_destroy(mdev.m_class);
rm_device_numb:
    unregister_chrdev_region(mdev.dev_num, 1);
    return -1;
}

static void
    __exit
    hello_world_exit(void) /* Destructor */
{
    device_destroy(mdev.m_class, mdev.dev_num); /* 03 */
    class_destroy(mdev.m_class);                /* 02 */
    unregister_chrdev_region(mdev.dev_num, 1);  /* 01 */

    pr_info("Goodbye\n");
}

module_init(hello_world_init);
module_exit(hello_world_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR(DRIVER_AUTHOR);
MODULE_DESCRIPTION(DRIVER_DESC);
```

+ File Makefile
```Makefile
EXTRA_CFLAGS = -Wall
obj-m = devicefile.o

KDIR = /lib/modules/`uname -r`/build

all:
	make -C $(KDIR) M=`pwd` modules

clean:
	make -C $(KDIR) M=`pwd` clean
```

+ Giải thích cách chạy.
  + Để xem class ta mới tạo thì: ls /sys/class/ 
  + Để xem device file và major number thì: ls –l /dev/ | grep 243


***Bước 3: Register File operations***
+ Nghĩa là ta đăng kí các hoạt động của file như open, read, write ... tới device file
+ Struct inode là struct chứa toàn bộ thông tin của một file

​<p align="center">
  <img src="Images/Screenshot_12.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ Struct cdev là một phần tử của **struct inode** và nó là struct đại diện cho **character device**

​<p align="center">
  <img src="Images/Screenshot_13.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ Struct file_operations là một phần tử của **struct cdev**. Staruct này định nghĩa toàn bộ các hoạt động của file (open/read/write/close/mmap/ioctl)

​<p align="center">
  <img src="Images/Screenshot_14.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

***Open operations***
+ Khởi tạo thiết bị hoặc làm cho thiết bị phản hồi cho read/write
+ Trong open có thể làm gì thì làm không thì bỏ trống
+ Nếu không làm gì thì open luôn thành công

​<p align="center">
  <img src="Images/Screenshot_16.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ Inode: con trỏ tới inode
+ File: Con trỏ tới file object
+ Return 0 nếu thành công, còn không return vè mã lỗi

***Read operations***
​<p align="center">
  <img src="Images/Screenshot_15.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ Parammeter
  + Filp: pointer của file obj
  + Buf: pointer của user buffer
  + Count: Số đọc từ user
  + offset: Con trỏ của vị trí file hiện tại mà từ đó quá trình đọc phải bắt đầu

+ Read method
  + Đọc ‘Counter’ byte từ thiết bị bắt đầu từ vị trí Offset’.
  + Cập nhật ‘Offset’ bằng cách thêm số byte đã đọc thành công
  + Trả về số byte đã đọc thành công
  + Trả về 0 nếu không có byte nào để đọc (EOF)
  + Trả về mã lỗi thích hợp (giá trị -ve) nếu có lỗi
  + Giá trị trả về nhỏ hơn 'count' không có nghĩa là đã xảy ra lỗi

​<p align="center">
  <img src="Images/Screenshot_17.png" alt="hello" style="width:500px; height:auto;"/>   
</p>


***Write operations***

​<p align="center">
  <img src="Images/Screenshot_19.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ Write method
  + Ghi ‘count’ byte vào thiết bị bắt đầu từ vị trí ‘Offset’.
  + Cập nhật ‘Offset’ bằng cách thêm số byte được ghi thành công
  + Trả về số byte được ghi thành công

​<p align="center">
  <img src="Images/Screenshot_18.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

***Lseek operations***
+ llseek method
  + Trong llseek method, Driver nên cập nhật con trỏ file bằng cách sử dụng thông tin 'offset' và 'wherece’
  + Trình xử lý llseek sẽ trả về vị trí tệp mới cập nhật hoặc có lỗi

​<p align="center">
  <img src="Images/Screenshot_20.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

​<p align="center">
  <img src="Images/Screenshot_21.png" alt="hello" style="width:500px; height:auto;"/>   
</p>


***Copy data***
​<p align="center">
  <img src="Images/Screenshot_22.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ Code cho phàn file operation
+ File fileoperation.c
```c
/******************************************************************************
 *  @author     hulatho hulatho@hula.com.vn
 *******************************************************************************/

#include <linux/module.h> /* Thu vien nay dinh nghia cac macro nhu module_init/module_exit */
#include <linux/fs.h>     /* Thu vien nay dinh nghia cac ham allocate major/minor number */
#include <linux/device.h> /* Thu vien nay dinh nghia cac ham class_create/device_create */
#include <linux/cdev.h>   /* Thu vien nay dinh nghia cac ham cdev_init/cdev_add */

#define DRIVER_AUTHOR "hulatho hulatho@hula.com.vn"
#define DRIVER_DESC "Hello world kernel module"

struct m_foo_dev
{
    dev_t dev_num;
    struct class *m_class;
    struct cdev m_cdev;
} mdev;

/*  Function Prototypes */
static int __init hello_world_init(void);
static void __exit hello_world_exit(void);

static int m_open(struct inode *inode, struct file *file);
static int m_release(struct inode *inode, struct file *file);
static ssize_t m_read(struct file *filp, char __user *user_buf, size_t size, loff_t *offset);
static ssize_t m_write(struct file *filp, const char *user_buf, size_t size, loff_t *offset);

static struct file_operations fops =
    {
        .owner = THIS_MODULE,
        .read = m_read,
        .write = m_write,
        .open = m_open,
        .release = m_release,
};

/* This function will be called when we open the Device file */
static int m_open(struct inode *inode, struct file *file)
{
    pr_info("System call open() called...!!!\n");
    return 0;
}

/* This function will be called when we close the Device file */
static int m_release(struct inode *inode, struct file *file)
{
    pr_info("System call close() called...!!!\n");
    return 0;
}

/* This function will be called when we read the Device file */
static ssize_t m_read(struct file *filp, char __user *user_buf, size_t size, loff_t *offset)
{
    pr_info("System call read() called...!!!\n");
    return 0;
}

/* This function will be called when we write the Device file */
static ssize_t m_write(struct file *filp, const char __user *user_buf, size_t size, loff_t *offset)
{
    pr_info("System call write() called...!!!\n");
    return size;
}

static int __init hello_world_init(void) /* Constructor */
{
    /* 1. Allocating device number (cat /pro/devices)*/
    if (alloc_chrdev_region(&mdev.dev_num, 0, 1, "m-cdev") < 0)
    {
        pr_err("Failed to alloc chrdev region\n");
        return -1;
    }
    pr_info("Major = %d Minor = %d\n", MAJOR(mdev.dev_num), MINOR(mdev.dev_num));

    /* 02.1 Creating cdev structure */
    cdev_init(&mdev.m_cdev, &fops);

    /* 02.2 Adding character device to the system */
    if ((cdev_add(&mdev.m_cdev, mdev.dev_num, 1)) < 0)
    {
        pr_err("Cannot add the device to the system\n");
        goto rm_device_numb;
    }

    /* 03. Creating struct class */
    if ((mdev.m_class = class_create(THIS_MODULE, "m_class")) == NULL)
    {
        pr_err("Cannot create the struct class for my device\n");
        goto rm_device_numb;
    }

    /* 04. Creating device*/
    if ((device_create(mdev.m_class, NULL, mdev.dev_num, NULL, "m_device")) == NULL)
    {
        pr_err("Cannot create my device\n");
        goto rm_class;
    }

    pr_info("Hello world kernel module\n");
    return 0;

rm_class:
    class_destroy(mdev.m_class);
rm_device_numb:
    unregister_chrdev_region(mdev.dev_num, 1);
    return -1;
}

static void
__exit hello_world_exit(void) /* Destructor */
{
    device_destroy(mdev.m_class, mdev.dev_num); /* 04 */
    class_destroy(mdev.m_class);                /* 03 */
    cdev_del(&mdev.m_cdev);                     /* 02 */
    unregister_chrdev_region(mdev.dev_num, 3);  /* 01 */

    pr_info("Goodbye\n");
}

module_init(hello_world_init);
module_exit(hello_world_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR(DRIVER_AUTHOR);
MODULE_DESCRIPTION(DRIVER_DESC);
```

+ File Makefile
```Makefile
EXTRA_CFLAGS = -Wall
obj-m = fileoperation.o

KDIR = /lib/modules/`uname -r`/build

all:
	make -C $(KDIR) M=`pwd` modules

clean:
	make -C $(KDIR) M=`pwd` clean
```

+ Cách chạy
```bash
$ make all
$ sudo insmod fileoperation.ko
$ sudo chmod 0777 /dev/m_device
$ echo "hello" > /dev/m_device           : khi echo thì nó sẽ chạy 3 câu lệnh, open write, close
$ cat /dev/m_device                      : khi cat thì nó sẽ chạy 3 câu lệnh, open read, close
$ Dmesg | tail                           : Ta sẽ thấy hiện các log in ra
```

### 3️⃣ Thực hành
+ Viết 1 character device driver và 1 app iwr user space để từ user space đọc ghi data từ character device driver
+ Bài này sẽ gồm 3 file là: cdevhula.c, Makefile và user.c

+ File user.c
```c
/******************************************************************************
 *  @brief      Userspace application to test the Device driver
 *
 *  @author     hulatho hulatho@hula.com.vn
 *******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
#include <stdio_ext.h>

#define CDEV_PATH "/dev/m_device"

int fd, option;
char write_buf[1024];
char read_buf[1024];

void printMenu()
{
  printf("****Please Enter the Option******\n");
  printf("        1. Write                 \n");
  printf("        2. Read                  \n");
  printf("        3. Exit                  \n");
  printf("*********************************\n");
  printf(">>> ");
}

int main()
{
  printf("*********************************\n");
  printf("********HuLa Linux***************\n");
  printf("*********************************\n");

  fd = open(CDEV_PATH, O_RDWR);
  if (fd < 0)
  {
    printf("Cannot open device file: %s...\n", CDEV_PATH);
    return -1;
  }

  while (1)
  {
      printMenu();

      scanf("%d", &option);
      switch (option)
      {
      case 1:
          printf("Enter the string to write into driver: ");
          scanf("  %[^\t\n]s", write_buf);
          printf("Data Writing ... ");
          write(fd, write_buf, strlen(write_buf) + 1);
          printf("Done!\n\n\n");
          break;

      case 2:
          printf("Data Reading ... ");
          read(fd, read_buf, 1024);
          printf("Done!\n");
          printf("Data: %s\n\n\n", read_buf);
          break;

      case 3:
          close(fd);
          exit(1);
          break;

      default:
          printf("Enter Valid option = %c\n", option);
          break;
      }
  }

  close(fd);

  return 0;
}
```

+ File Makefile
```Makefile
EXTRA_CFLAGS = -Wall
obj-m = cdevhula.o

KDIR := /lib/modules/`uname -r`/build
CC := gcc

all:
	make -C $(KDIR) M=`pwd` modules
	$(CC) -o user user.c

clean:
	make -C $(KDIR) M=`pwd` clean
	rm -rf user
```

+ File cdevhula.c
```c
/******************************************************************************
 *  @brief      Simple Linux device driver
 *
 *  @author     hulatho hulatho@hula.com.vn
 *******************************************************************************/

#include <linux/module.h>  /* Thu vien nay dinh nghia cac macro nhu module_init/module_exit */
#include <linux/fs.h>      /* Thu vien nay dinh nghia cac ham allocate major/minor number */
#include <linux/device.h>  /* Thu vien nay dinh nghia cac ham class_create/device_create */
#include <linux/cdev.h>    /* Thu vien nay dinh nghia cac ham kmalloc */
#include <linux/slab.h>    /* Thu vien nay dinh nghia cac ham cdev_init/cdev_add */
#include <linux/uaccess.h> /* Thu vien nay dinh nghia cac ham copy_to_user/copy_from_user */

#define DRIVER_AUTHOR "hulatho hulatho@hula.com.vn"
#define DRIVER_DESC "Hello world kernel module"

#define NPAGES 1

struct m_foo_dev
{
    int size;
    char *kmalloc_ptr;
    dev_t dev_num;
    struct class *m_class;
    struct cdev m_cdev;
} mdev;

/*  Function Prototypes */
static int __init hello_world_init(void);
static void __exit hello_world_exit(void);

static int m_open(struct inode *inode, struct file *file);
static int m_release(struct inode *inode, struct file *file);
static ssize_t m_read(struct file *filp, char __user *user_buf, size_t size, loff_t *offset);
static ssize_t m_write(struct file *filp, const char *user_buf, size_t size, loff_t *offset);

static struct file_operations fops =
{
    .owner = THIS_MODULE,
    .read = m_read,
    .write = m_write,
    .open = m_open,
    .release = m_release,
};

/* This function will be called when we open the Device file */
static int m_open(struct inode *inode, struct file *file)
{
    pr_info("System call open() called...!!!\n");
    return 0;
}

/* This function will be called when we close the Device file */
static int m_release(struct inode *inode, struct file *file)
{
    pr_info("System call close() called...!!!\n");
    return 0;
}

/* This function will be called when we read the Device file */
static ssize_t m_read(struct file *filp, char __user *user_buffer, size_t size, loff_t *offset)
{
    size_t to_read;

    pr_info("System call read() called...!!!\n");

    /* Check size doesn't exceed our mapped area size */
    to_read = (size > mdev.size - *offset) ? (mdev.size - *offset) : size;

    /* Copy from mapped area to user buffer */
    if (copy_to_user(user_buffer, mdev.kmalloc_ptr + *offset, to_read))
        return -EFAULT;

    *offset += to_read;

    return to_read;
}

/* This function will be called when we write the Device file */
static ssize_t m_write(struct file *filp, const char __user *user_buffer, size_t size, loff_t *offset)
{
    size_t to_write;

    pr_info("System call write() called...!!!\n");

    /* Check size doesn't exceed our mapped area size */
    to_write = (size + *offset > NPAGES * PAGE_SIZE) ? (NPAGES * PAGE_SIZE - *offset) : size;

    /* Copy from user buffer to mapped area */
    memset(mdev.kmalloc_ptr, 0, NPAGES * PAGE_SIZE);
    if (copy_from_user(mdev.kmalloc_ptr + *offset, user_buffer, to_write) != 0)
        return -EFAULT;
    
    

    pr_info("Data from usr: %s", mdev.kmalloc_ptr);

    *offset += to_write;
    mdev.size = *offset;

    return to_write;
}

static int
    __init
    hello_world_init(void) /* Constructor */
{
    /* 1. Allocating device number (cat /pro/devices)*/
    if (alloc_chrdev_region(&mdev.dev_num, 0, 1, "m-cdev") < 0)
    {
        pr_err("Failed to alloc chrdev region\n");
        return -1;
    }
    pr_info("Major = %d Minor = %d\n", MAJOR(mdev.dev_num), MINOR(mdev.dev_num));

    /* 02.1 Creating cdev structure */
    cdev_init(&mdev.m_cdev, &fops);

    /* 02.2 Adding character device to the system*/
    if ((cdev_add(&mdev.m_cdev, mdev.dev_num, 1)) < 0)
    {
        pr_err("Cannot add the device to the system\n");
        goto rm_device_numb;
    }

    /* 03. Creating struct class */
    if ((mdev.m_class = class_create(THIS_MODULE, "m_class")) == NULL)
    {
        pr_err("Cannot create the struct class for my device\n");
        goto rm_device_numb;
    }

    /* 04. Creating device*/
    if ((device_create(mdev.m_class, NULL, mdev.dev_num, NULL, "m_device")) == NULL)
    {
        pr_err("Cannot create my device\n");
        goto rm_class;
    }

    /* 05. Creating Physical memory tao vung nho duoi kernel */
    if ((mdev.kmalloc_ptr = kmalloc(1024, GFP_KERNEL)) == 0)
    {
        pr_err("Cannot allocate memory in kernel\n");
        goto rm_device;
    }

    pr_info("Hello world kernel module\n");
    return 0;

rm_device:
    device_destroy(mdev.m_class, mdev.dev_num);
rm_class:
    class_destroy(mdev.m_class);
rm_device_numb:
    unregister_chrdev_region(mdev.dev_num, 1);
    return -1;
}

static void
    __exit
    hello_world_exit(void) /* Destructor */
{
    kfree(mdev.kmalloc_ptr);                    /* 05 */
    device_destroy(mdev.m_class, mdev.dev_num); /* 04 */
    class_destroy(mdev.m_class);                /* 03 */
    cdev_del(&mdev.m_cdev);                     /* 02 */
    unregister_chrdev_region(mdev.dev_num, 3);  /* 01 */

    pr_info("Goodbye\n");
    ;
}

module_init(hello_world_init);
module_exit(hello_world_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR(DRIVER_AUTHOR);
MODULE_DESCRIPTION(DRIVER_DESC);
```


+ Cách chạy
```bash
$ make all
$ sudo insmod cdevhula.ko
$ sudo chmod 0777 /dev/m_device
$ ./user
```

​<p align="center">
  <img src="Images/Screenshot_24.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

## ✔️ Conclusion
Ở bài này chúng ta đã biết cách tạo ra 1 Character Device Driver file. Tiếp theo chúng ta sẽ áp dụng và có thể nháy led nhé.

## 💯 Exercise
+ Thực hành theo bài viết

## 📺 NOTE
+ N/A

## 📌 Reference

[1] i.MX Linux Reference Manual

[2] https://vimentor.com/vi/lesson/cap-phat-device-number

[3] https://vimentor.com/vi/lesson/cap-phat-dong-device-number-1
