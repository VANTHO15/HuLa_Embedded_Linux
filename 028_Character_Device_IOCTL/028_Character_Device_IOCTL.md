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
+ Bài này gồm 3 file là hello.c, Makefile, usr_app.c và ta sẽ thêm IOCTL vào
+ File Makefile
```Makefile
EXTRA_CFLAGS = -Wall
obj-m = hello.o

KDIR := /lib/modules/`uname -r`/build
CC := gcc

all:
	make -C $(KDIR) M=`pwd` modules
	$(CC) -o app usr_app.c

clean:
	make -C $(KDIR) M=`pwd` clean
	rm -rf app
```

+ File hello.c
```c
/******************************************************************************
*  @brief      Simple Linux device driver
*
*  @author     hulatho - hulatho@hula.com.vn
*******************************************************************************/

#include <linux/module.h>  /* Thu vien nay dinh nghia cac macro nhu module_init/module_exit */
#include <linux/fs.h>      /* Thu vien nay dinh nghia cac ham allocate major/minor number */
#include <linux/device.h>  /* Thu vien nay dinh nghia cac ham class_create/device_create */
#include <linux/cdev.h>    /* Thu vien nay dinh nghia cac ham kmalloc */
#include <linux/slab.h>    /* Thu vien nay dinh nghia cac ham cdev_init/cdev_add */
#include <linux/uaccess.h> /* Thu vien nay dinh nghia cac ham copy_to_user/copy_from_user */

#define DRIVER_AUTHOR "hulatho hulatho@hula.com.vn"
#define DRIVER_DESC   "Hello world kernel module"

#define NPAGES  1

#define WR_VALUE _IOW('a', '1', int32_t *)
#define RD_VALUE _IOR('a', '2', int32_t *)

static int32_t value = 0;

struct m_foo_dev {
    int32_t size;
    char *kmalloc_ptr;
    dev_t dev_num;
    struct class *m_class;
    struct cdev m_cdev;
} mdev;

/*  Function Prototypes */
static int      __init hello_world_init(void);
static void     __exit hello_world_exit(void);
static int      m_open(struct inode *inode, struct file *file);
static int      m_release(struct inode *inode, struct file *file);
static ssize_t  m_read(struct file *filp, char __user *user_buf, size_t size,loff_t *offset);
static ssize_t  m_write(struct file *filp, const char *user_buf, size_t size, loff_t *offset);
static long     m_ioctl(struct file *file, unsigned int cmd, unsigned long arg);

static struct file_operations fops =
{
    .owner      = THIS_MODULE,
    .read       = m_read,
    .write      = m_write,
    .open       = m_open,
    .release    = m_release,
    .unlocked_ioctl = m_ioctl,
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

    *offset += to_write;
    mdev.size = *offset;

	return to_write;
}

static long m_ioctl(struct file *file, unsigned int cmd, unsigned long arg)
{
    switch(cmd) {
        case WR_VALUE:
            if (copy_from_user(&value , (int32_t*)arg, sizeof(value))) {
                pr_err("Data Write : Err!\n");
            }
            pr_info("Value = %d\n", value);
            break;

        case RD_VALUE:
            if (copy_to_user((int32_t*)arg, &value, sizeof(value))) {
                    pr_err("Data Read : Err!\n");
            }
            break;

        default:
                pr_info("Default\n");
                break;
    }

    return 0;
}

static int 
__init hello_world_init(void)   /* Constructor */
{   
    /* 1. Allocating device number (cat /pro/devices)*/
    if (alloc_chrdev_region(&mdev.dev_num, 0, 1, "m-cdev") < 0) {
	    pr_err("Failed to alloc chrdev region\n");
	    return -1;
    }
    pr_info("Major = %d Minor = %d\n", MAJOR(mdev.dev_num), MINOR(mdev.dev_num));

    /* 02.1 Creating cdev structure */
    cdev_init(&mdev.m_cdev, &fops);

    /* 02.2 Adding character device to the system*/
    if ((cdev_add(&mdev.m_cdev, mdev.dev_num, 1)) < 0) {
        pr_err("Cannot add the device to the system\n");
        goto rm_device_numb;
    }

    /* 03. Creating struct class */
    if ((mdev.m_class = class_create(THIS_MODULE, "m_class")) == NULL) {
        pr_err("Cannot create the struct class for my device\n");
        goto rm_device_numb;
    }

    /* 04. Creating device*/
    if ((device_create(mdev.m_class, NULL, mdev.dev_num, NULL, "m_device")) == NULL) {
        pr_err("Cannot create my device\n");
        goto rm_class;
    }

    /* 05. Creating Physical memory*/
    if((mdev.kmalloc_ptr = kmalloc(NPAGES * PAGE_SIZE , GFP_KERNEL)) == 0){
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
__exit hello_world_exit(void)   /* Destructor */
{
    kfree(mdev.kmalloc_ptr);                        /* 05 */
    device_destroy(mdev.m_class, mdev.dev_num);     /* 04 */
    class_destroy(mdev.m_class);                    /* 03 */
    cdev_del(&mdev.m_cdev);                         /* 02 */
    unregister_chrdev_region(mdev.dev_num, 3);      /* 01 */

    pr_info("Goodbye\n");;
}

module_init(hello_world_init);
module_exit(hello_world_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR(DRIVER_AUTHOR);
MODULE_DESCRIPTION(DRIVER_DESC); 
MODULE_VERSION("1.0"); 
```

+ FIle usr_app.c
```c
/******************************************************************************
*  @brief      Userspace application to test the Device driver
*
*  @author     hulatho - hulatho@hula.com.vn
*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
#include <stdio_ext.h>
#include<sys/ioctl.h>

#define WR_VALUE _IOW('a','1',int32_t*)
#define RD_VALUE _IOR('a','2',int32_t*)

#define CDEV_PATH "/dev/m_device"

int fd, option;
int number, foo;
char write_buf[1024];
char read_buf[1024];

void printMenu()
{

    printf("****Please Enter the Option******\n");
    printf("        1.  Write                 \n");
    printf("        2.  Read                  \n");
    printf("        3.  Ioctl write           \n");
    printf("        4.  Ioctl read            \n");
    printf("        99. Exit                  \n");
    printf("*********************************\n");
    printf(">>> ");
}

int main()
{   
    printf("*********************************\n");
    printf("*******Linux From Scratch*******\n\n");

    fd = open(CDEV_PATH, O_RDWR);
    if (fd < 0) {
        printf("Cannot open device file: %s...\n", CDEV_PATH);
        return -1;
    }

    while(1) {
        printMenu();

        scanf("%d",&option); 
        switch (option) {
            case 1:
                printf("Enter the string to write into driver: ");
                scanf("  %[^\t\n]s", write_buf);
                printf("Data Writing ... ");
                write(fd, write_buf, strlen(write_buf)+1);
                printf("Done!\n\n\n");
                break;

            case 2:
                printf("Data Reading ... ");
                read(fd, read_buf, 1024);                
                printf("Done!\n");
                printf("Data: %s\n\n\n", read_buf);
                break;
            case 3:
                printf("Writting value using ioctl\n");
                printf("Enter number: ");
                scanf("%d", &number); 
                ioctl(fd, WR_VALUE, (int32_t*)&number);
                printf("Done!\n\n\n");
                break;

            case 4:
                printf("Reading value using ioctl\n");
                ioctl(fd, RD_VALUE, (int32_t*)&foo);
                printf("Value: %d ... Done!\n\n\n", foo);
                break;

            case 99:
                close(fd);
                exit(1);               
                break;

            default:
                printf("Enter Valid option = %c\n",option);
                break;
        }
    }
    
    close(fd);

    return 0;
}
```

***Led Blynk IOCTL***
+ Bài này gồm 5 file là led.c, led.h, Kbuild, Makefile, user_app.c
+ File led.c
```c
#include <linux/module.h> /* This module defines functions such as module_init/module_exit */
#include <linux/io.h>     /* This module defines functions such as ioremap/iounmap */
#include <linux/fs.h>     /* This module defines major/minor numbers allocation functions */
#include <linux/device.h> /* This module defines functions such as class_create/device_create */
#include <linux/cdev.h>   /* This module defines functions such as cdev_init/cdev_add */
#include <linux/slab.h>   /* This module defines functions such as cdev_init/cdev_add */
#include "led.h"          /* LED modules */

#define DRIVER_AUTHOR "thonv thonv@gmail.com"
#define DRIVER_DESC "LED blinking with Ioctl()"

// a 0 a 1 is command line
#define LED_ON _IOW('a', '1', int32_t *)
#define LED_OFF _IOW('a', '0', int32_t *)

uint32_t __iomem *base_addr;
uint32_t __iomem *base_addr_clk;
uint32_t __iomem *base_addr_mux_gpio1_io9;

struct m_foo_dev
{
    dev_t dev_num;
    struct class *m_class;
    struct cdev m_cdev;
} mdev;

/*  Function Prototypes */
static int __init led_init(void);
static void __exit led_exit(void);
static long m_ioctl(struct file *file, unsigned int cmd, unsigned long arg);

static struct file_operations fops =
    {
        .owner = THIS_MODULE,
        .unlocked_ioctl = m_ioctl,
};

static long m_ioctl(struct file *file, unsigned int cmd, unsigned long arg)
{
    switch (cmd)
    {
        case LED_ON:
            pr_info("LED on\n");
            *(base_addr + GPIO_DR_OFFSET / 4) |= LED;
            break;

        case LED_OFF:
            pr_info("LED off\n");
            *(base_addr + GPIO_DR_OFFSET / 4) &=~ LED; 
            break;

        default:
            pr_info("Default\n");
            break;
    }

    return 0;
}

/* Constructor */
static int __init led_init(void)
{
    /* 1. Allocating device number (cat /pro/devices)*/
    if (alloc_chrdev_region(&mdev.dev_num, 0, 1, "m-cdev1") < 0)
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
    if ((mdev.m_class = class_create(THIS_MODULE, "m_class1")) == NULL)
    {
        pr_err("Cannot create the struct class for my device\n");
        goto rm_device_numb;
    }

    /* 04. Creating device*/
    if ((device_create(mdev.m_class, NULL, mdev.dev_num, NULL, "m_device1")) == NULL)
    {
        pr_err("Cannot create my device\n");
        goto rm_class;
    }

    /* 05. Map GPIO-0 adress */
    base_addr = ioremap(GPIO_0_ADDR_BASE, GPIO_0_ADDR_SIZE);
    if (!base_addr)
    {
        goto rm_device;
        return -ENOMEM;
    }

    /* 05 Set led mode as ouput then turn it on */
    base_addr = ioremap(GPIO_1_ADDR_BASE, GPIO_1_ADDR_SIZE);
	base_addr_clk = ioremap(CCM_CCGRn_ADDR_BASE, CCM_CCGRn_ADDR_SIZE);
	base_addr_mux_gpio1_io9 = ioremap(IOMUXC_SW_MUX_CTL_PAD_GPIO1_IO09_BASE, IOMUXC_SW_MUX_CTL_PAD_GPIO1_IO09_SIZE);

	*(base_addr_clk + GPIO1_ENABLE_CLOCK_OFFSET / 4) |= CLK;
	*(base_addr_clk + GPIO1_SET_CLOCK_OFFSET / 4) |= CLK;

	*(base_addr_mux_gpio1_io9) |= 1 << 4;  /* ENABLED SION */
	*(base_addr_mux_gpio1_io9) &=~ 0x07;  /* Select signal GPIO1_IO09 */

	*(base_addr + GPIO_GDIR1_OFFSET / 4) |= LED;

    pr_info("Hello! Initizliaze successfully!\n");
    return 0;

rm_device:
    device_destroy(mdev.m_class, mdev.dev_num);
rm_class:
    class_destroy(mdev.m_class);
rm_device_numb:
    unregister_chrdev_region(mdev.dev_num, 1);
    return -1;
}

/* Destructor */
static void __exit led_exit(void)
{
    iounmap(base_addr);                         /* 05 */
    device_destroy(mdev.m_class, mdev.dev_num); /* 04 */
    class_destroy(mdev.m_class);                /* 03 */
    cdev_del(&mdev.m_cdev);                     /* 02 */
    unregister_chrdev_region(mdev.dev_num, 3);  /* 01 */

    pr_info("Good bye ThoNV!!!\n");
}

module_init(led_init);
module_exit(led_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR(DRIVER_AUTHOR);
MODULE_DESCRIPTION(DRIVER_DESC);
MODULE_VERSION("1.0");
```

+ File led.h
```h
#ifndef __LED_MODULE_H__
#define __LED_MODULE_H__

#define CCM_CCGRn_ADDR_BASE       0x30380000
#define GPIO_1_ADDR_BASE          0x30200000

#define GPIO1_ENABLE_CLOCK_OFFSET (0x40B0)
#define GPIO1_SET_CLOCK_OFFSET    (0x40B4)

#define GPIO_DR_OFFSET            (0x00)	    
#define GPIO_GDIR1_OFFSET	      (0x04)	/* 0 input ; 1 output */	

#define GPIO_1_ADDR_SIZE	      (0x3020FFFF - GPIO_1_ADDR_BASE)
#define CCM_CCGRn_ADDR_SIZE       (0x3038FFFF - CCM_CCGRn_ADDR_BASE)

#define IOMUXC_SW_MUX_CTL_PAD_GPIO1_IO09_BASE    0x3033003C
#define IOMUXC_SW_MUX_CTL_PAD_GPIO1_IO09_SIZE    (0x30330040 - 0x3033003C)

#define LED     (1 << 9)   /* GPIO_1_IO9 */
#define CLK     (0x00003333)

#endif  /* __LED_MODULE_H__ */
```

+ File Kbuild
```Kbuild
EXTRA_CFLAGS=-Wall

obj-m := led.o
```

+ File Makefile
```Makefile
KERNELDIR ?= /home/thonv12/yocto_imx/build-xwayland/tmp/work/mys_8mmx-poky-linux/linux-imx/5.4-r0/build

all:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules
	$(CC) -o app usr_app.c
	
clean:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) clean
	rm -rf app
```

+ File usrer_app.c
```c
/******************************************************************************
 *  @brief      Userspace application to test the Device driver
 *
 *  @author     thonv thonv@gmail.com
 *******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
#include <stdio_ext.h>
#include <sys/ioctl.h>

#define LED_ON _IOW('a', '1', int32_t *)
#define LED_OFF _IOW('a', '0', int32_t *)

#define CDEV_PATH "/dev/m_device1"

int fd, option;
int number, foo;
char write_buf[1024];
char read_buf[1024];

void printMenu()
{

    printf("**** Please Enter the Option *****\n");
    printf("        1.  LED on                \n");
    printf("        0.  LED off               \n");
    printf("        99. Exit                  \n");
    printf("**********************************\n");
    printf(">>> ");
}

int main()
{
    printf("*********************************\n");
    printf("******* Linux ThoNV12 *******\n\n");
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
            printf("Turn LED on\n");
            ioctl(fd, LED_ON, NULL);
            printf("Done!\n\n\n");
            break;

        case 0:
            printf("Turn LED off\n");
            ioctl(fd, LED_OFF, NULL);
            printf("Done!\n\n\n");
            break;

        case 2:
            close(fd);
            exit(1);
            break;
        default:
            printf("Enter invalid option: %c\n", option);
            break;
        }
    }

    close(fd);
    return 0;
}
```


***IOTCL Blynk Led Full Option***
+ Bài này gồm 4 file là led_app.c, led_driver_ioctl.c, led_driver_ioctl.h và Makefile
+ FIle led_app.c
```c
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <getopt.h>
#include <fcntl.h>
#include <string.h>

#include "led_driver_ioctl.h"

const char    * short_opt = "sgch";

struct option   long_opt[] = {
        {"setcfg",  no_argument      , NULL, 's'},
        {"getcfg",  no_argument      , NULL, 'g'},
        {"clrcfg",  no_argument      , NULL, 'c'},
        {"help",    no_argument      , NULL, 'h'},
        {NULL,      0,                 NULL,  0 }
};

#define LED_DEV   "/dev/led_dev"

void set_config(int fd);
void get_config(int fd);
void clr_config(int fd);

void usage(int argc, char * argv[])
{
        printf("Usage: %s [OPTIONS]\n", argv[0]);
        printf("  -h, --help        print this help and exit\n");
        printf("  -s  --setcfg      set configuration\n");
        printf("  -g  --getcfg      get configuration\n");
        printf("  -c  --clrcfg      clear configuration to default\n");
        printf("\n");
}

void set_config(int fd){
        int v;
        struct led_cfg led;

        printf("Enter Mode: ");
        scanf("%d", &v);
        getchar();
        led.mode = v;

        printf("Enter blink period: ");
        scanf("%d", &v);
        getchar();
        led.blink_period = v;

        if (ioctl(fd, LED_SET_CONFIG, &led) == -1) {
                perror("led_apps ioctl set");
        }
}


void get_config(int fd){
        struct led_cfg led;

        if (ioctl(fd, LED_GET_CONFIG, &led) == -1) {
                perror("led_apps ioctl get");
        } else {
                printf("Mode          : %d\n", led.mode);
                printf("Blink_period  : %d\n", led.blink_period);
        }
}


void clr_config(int fd){
        if (ioctl(fd, LED_CLR_CONFIG) == -1) {
                perror("led_apps ioctl clr");
        }
        printf("Set configuration to default value\n");
}


int main(int argc, char * argv[])
{
        int fd;
        int c;
        if (argc <= 1) {
                usage(argc, argv);
                exit(-1);
        }

        if ((fd = open(LED_DEV, O_RDWR)) == -1) {
                perror("led_apps open");
                exit(-1);
        }

        while ((c = getopt_long(argc, argv, short_opt, long_opt, NULL)) != -1) {
                switch (c) {
                case -1:       /* no more arguments */
                case 0:        /* long options toggles */
                        break;
                case 's':
                        set_config(fd);
                        break;
                case 'g':
                        get_config(fd);
                        break;
                case 'c':
                        clr_config(fd);
                        break;
                case 'h':
                        usage(argc, argv);
                        return (0);

                case ':':
                case '?':
                        fprintf(stderr, "Try `%s --help' for more information.\n", argv[0]);
                        return (-2);

                default:
                        fprintf(stderr, "%s: invalid option -- %c\n", argv[0], c);
                        fprintf(stderr, "Try `%s --help' for more information.\n", argv[0]);
                        return (-2);
                };
        };

        return (0);
}
```

+ File led_driver_ioctl.c
```c
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/init.h>

#include <linux/types.h>        /* dev_t type*/
#include <linux/kdev_t.h>       /* macro for major, minor ...*/
#include <linux/fs.h>           /* struct file, struct file_operations, struct inode..*/
#include <linux/cdev.h>         /* struct cdev*/
#include <linux/slab.h>         /* kmalloc()*/
#include <linux/device.h>       /* device_create(), device_destroy()*/

#include <asm/uaccess.h>

#include "led_driver_ioctl.h"

MODULE_LICENSE("GPL");
MODULE_AUTHOR("HuLaTho");
MODULE_DESCRIPTION(" Demo using ioclt in kernel module");

#define DEV_NAME "led_dev"
#define DEV_PER 0666    /* permission for /dev/char_dev_sem */
static dev_t dev_num;   /* Global variable store device number*/

#define BLINK_PERIOD    1000 /*ms*/

/* Create new data type for char dev*/
struct led_dev {
        struct led_cfg cfg;
        struct cdev cdev;
};

struct led_dev* dev; /* Global pointer to chrdev struct, allocated in module_init */
static struct class *led_dev_class; /* Global variable for the device class*/


static int my_open(struct inode *inode, struct file *filp)
{
        struct led_dev *pdev;

        pdev = container_of(inode->i_cdev, struct led_dev, cdev);
        filp->private_data = pdev;  /* Store for other methods*/

        return 0;
}

static int my_release(struct inode *inode, struct file *filp)
{
        return 0;
}

static long my_ioctl(struct file *filp, unsigned int cmd, unsigned long arg)
{
        struct led_dev* pdev = filp->private_data;

        switch (cmd) {
        case LED_GET_CONFIG:
                if (copy_to_user((struct led_cfg*)arg, &pdev->cfg, sizeof(struct led_cfg))) {
                        return -EACCES;
                }

                 /* Code for control led */

                break;
        case LED_CLR_CONFIG:
                pdev->cfg.mode = LED_OFF;
                pdev->cfg.blink_period = BLINK_PERIOD;

                 /* Code for control led */

                break;
        case LED_SET_CONFIG:
                if (copy_from_user(&pdev->cfg, (struct led_cfg *)arg, sizeof(struct led_cfg))) {
                        return -EACCES;
                }

                /* Code for control led */

                break;
        default:
                return -EINVAL;
        }

        return 0;
}

/* Set permission for file */
static char *set_devnode(struct device *dev, umode_t *mode)
{
        if (mode) *mode = DEV_PER;
        return NULL;
}

static struct file_operations query_fops = {
        .owner = THIS_MODULE,
        .open = my_open,
        .release = my_release,
        .unlocked_ioctl = my_ioctl
};

static int __init query_ioctl_init(void)
{
        int result = 0;

        /* Dynamic allocate device number*/
        result = alloc_chrdev_region(&dev_num, 0, 1, DEV_NAME);

        if ( result < 0) {
                printk(KERN_WARNING "Can't get major %d \n", MAJOR(dev_num));
                goto error_get_major;
        }

        printk(KERN_WARNING DEV_NAME": MAJOR: %d MINOR: %d ", MAJOR(dev_num), 0);

        /* Create class device */
        led_dev_class = class_create(THIS_MODULE, DEV_NAME);
        if (!led_dev_class) {
                printk(KERN_WARNING DEV_NAME ": Error to create class\n");
                goto error_class_create;
        }

        /* Set devnode for device*/
        led_dev_class->devnode = set_devnode;

        /* Create devide in /dev/ */
        if (device_create(led_dev_class, NULL, dev_num, NULL, DEV_NAME) == NULL) {
                printk(KERN_WARNING DEV_NAME ": Error to create device\n");
                goto error_device_create;
        }

        /* Allocate the devide*/
        dev = kmalloc(sizeof(struct led_dev), GFP_KERNEL);
        if (!dev) {
                result = -ENOMEM;
                goto error_kmalloc_dev; /* Make this more graceful */
        }

        /* Initialize device */
        dev->cfg.mode = LED_OFF;
        dev->cfg.blink_period = BLINK_PERIOD;

        /* Register char driver*/
        cdev_init(&dev->cdev, &query_fops);
        dev->cdev.owner = THIS_MODULE;
        dev->cdev.ops = &query_fops;

        /* we already created and initialized our cdev structure now we need to
        add it to the kernel*/
        result = cdev_add(&dev->cdev, dev_num, 1);
        if (result) {
                printk(KERN_WARNING DEV_NAME ": Error to add led_dev\n");
                goto error_cdev_add;
        }

        return 0; /* Success */

error_cdev_add:
        kfree(dev);
error_kmalloc_dev:
        device_destroy(led_dev_class, dev_num);
error_device_create:
        class_destroy(led_dev_class);
error_class_create:
        unregister_chrdev_region(dev_num, 1);
error_get_major:
        return result;
}

static void __exit query_ioctl_exit(void)
{
        kfree(dev);
        cdev_del(&dev->cdev);
        device_destroy(led_dev_class, dev_num);
        class_destroy(led_dev_class);
        unregister_chrdev_region(dev_num, 1);
        printk(KERN_INFO DEV_NAME ": unregistered\n");
}

module_init(query_ioctl_init);
module_exit(query_ioctl_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("HuLaTho");
MODULE_DESCRIPTION("Ioctl() Char Driver");
```

+ File led_driver_ioctl.h
```h
#ifndef QUERY_IOCTL_H
#define QUERY_IOCTL_H

#include <linux/ioctl.h>

#define LED_OFF         0
#define LED_ON          1
#define LED_BLINK       2

struct led_cfg {
        int mode;
        int blink_period;
};

#define LED_IOC_MAGIC 'k'

#define LED_CLR_CONFIG _IO (LED_IOC_MAGIC, 1)
#define LED_GET_CONFIG _IOR(LED_IOC_MAGIC, 2, struct led_cfg *)
#define LED_SET_CONFIG _IOW(LED_IOC_MAGIC, 3, struct led_cfg *)

#endif
```

+ File Makefile
```Makefile
obj-m := led_driver_ioctl.o

KERNELDIR ?= /lib/modules/$(shell uname -r)/build

all: led_app
	$(MAKE) -C $(KERNELDIR)  M=$(PWD) modules
clean:
	$(MAKE) -C $(KERNELDIR)  M=$(PWD) clean
	${RM} led_app
```

***IOCTL Control Led dùng Thư viện Gpio***
+ Bài này gồm 3 file là led_control_app.c, led_driver.c, Makefile. Các bạn đọc nghiên cứu thêm nhé. Ở bài sau nữa mình sẽ nói về các thư viện này. Bài này chủ yếu đưa ra bộ xương để mọi người xem theo các làm và hiểu code

+ File led_control_app.c
```c
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <getopt.h>
#include <fcntl.h>
#include <string.h>

const char    * short_opt = "m:b:p:sh";
struct option   long_opt[] = {
    {"mode",          required_argument, NULL, 'm'},
    {"brighness",     required_argument, NULL, 'b'},
    {"peroid",        required_argument, NULL, 'p'},
    {"status",        no_argument      , NULL, 's'},
    {"help",          no_argument      , NULL, 'h'},
    {NULL,            0,                 NULL, 0  }
};

#define LED_SYSFS       "/sys/led_driver"
#define LED_MODE        LED_SYSFS"/mode"
#define LED_PERIOD      LED_SYSFS"/blink_period"
#define LED_BRIGHTNESS  LED_SYSFS"/brightness"
#define LED_LABLE       LED_SYSFS"/lable"

int set_mode(char* optarg);
int set_brighness(char* optarg);
int set_peroid(char* optarg);
int get_status(void);

void usage(int argc, char * argv[])
{
    printf("Usage: %s [OPTIONS]\n", argv[0]);
    printf("Eg:\n  %s -m flash -p 500\n", argv[0]);
    printf("  -h, --help                print this help and exit\n");
    printf("  -m mode                   set mode: on, off, flash\n");
    printf("  -b brighness              set brighness: 0 - 100\n");
    printf("  -p peroid                 set peroid: 10 - 1000 ms\n");
    printf("  -s status                 get LED status\n");
    printf("\n");
}
int main(int argc, char * argv[])
{
    int c;
    if (argc <= 1) {
        usage(argc, argv);
    }

    while ((c = getopt_long(argc, argv, short_opt, long_opt, NULL)) != -1) {
        switch (c) {
        case -1:       /* no more arguments */
        case 0:        /* long options toggles */
            break;
        case 'm':
            set_mode(optarg);
            break;
        case 'b':
            set_brighness(optarg);
            break;
        case 'p':
            set_peroid(optarg);
            break;
        case 's':
            get_status();
            break;
        case 'h':
            usage(argc, argv);
            return (0);

        case ':':
        case '?':
            fprintf(stderr, "Try `%s --help' for more information.\n", argv[0]);
            return (-2);

        default:
            fprintf(stderr, "%s: invalid option -- %c\n", argv[0], c);
            fprintf(stderr, "Try `%s --help' for more information.\n", argv[0]);
            return (-2);
        };
    };

    return (0);
}

int set_mode(char * optarg)
{
    int count = strlen(optarg);

    if ((strncmp(optarg, "on", count - 1) != 0)   &&
        (strncmp(optarg, "off", count - 1) != 0)  &&
        (strncmp(optarg, "flash", count - 1) != 0)) {

        fprintf(stderr, "wrong mode: %s!\n", optarg );
        exit(-1);
    }

    FILE* f = fopen(LED_MODE, "w");
    if (f == NULL) {
        fprintf(stderr, "Unable to open %s\n", LED_MODE);
        return 1;
    }
    fprintf(f, "%s", optarg);
    fclose(f);
    printf("Set mode as \"%s\": SUCCESS\n", optarg);
    return 0;
}

int set_brighness(char * optarg)
{
    int value = atoi(optarg);
    if (value < 0 || value > 100) {
        fprintf(stderr, "wrong value: %d\n", value);
        exit(-1);
    }

    FILE* f = fopen(LED_BRIGHTNESS, "w");
    if (f == NULL) {
        fprintf(stderr, "Unable to open %s\n", LED_BRIGHTNESS);
        return 1;
    }
    fprintf(f, "%s", optarg);
    fclose(f);
    printf("Set brighness as \"%d\": SUCCESS\n", atoi(optarg));
}

int set_peroid(char* optarg)
{
    int value = atoi(optarg);
    if (value < 10 || value > 1000) {
        fprintf(stderr, "wrong value: %d\n", value);
        exit(-1);
    }

    FILE* f = fopen(LED_PERIOD, "w");
    if (f == NULL) {
        fprintf(stderr, "Unable to open %s\n", LED_PERIOD);
        return 1;
    }
    fprintf(f, "%s", optarg);
    fclose(f);
    printf("set peroid as \"%d\": SUCCESS\n", atoi(optarg));
}

int get_status(void)
{
    FILE* f;
    #define BUF_SISE 20
    char  buff[BUF_SISE];
    printf("Status of LED\n");

    if ((f = fopen(LED_LABLE, "r")) == NULL) {
        fprintf(stderr, "Unable to open %s\n", LED_LABLE);
        return 1;
    } else {
        fgets(buff, BUF_SISE, f);
        printf("\tLable:\t\t %s", buff);
        fclose(f);
    }

    if ((f = fopen(LED_MODE, "r")) == NULL) {
        fprintf(stderr, "Unable to open %s\n", LED_MODE);
        return 1;
    } else {
        fgets(buff, BUF_SISE, f);
        printf("\tMode:\t\t %s", buff);
        fclose(f);
    }

    if ((f = fopen(LED_BRIGHTNESS, "r")) == NULL) {
        fprintf(stderr, "Unable to open %s\n", LED_BRIGHTNESS);
        return 1;
    } else {
        fgets(buff, BUF_SISE, f);
        printf("\tBrightness:\t %s", buff);
        fclose(f);
    }
    if ((f = fopen(LED_PERIOD, "r")) == NULL) {
        fprintf(stderr, "Unable to open %s\n", LED_PERIOD);
        return 1;
    } else {
        fgets(buff, BUF_SISE, f);
        printf("\tBlink peroid:\t %s", buff);
        fclose(f);
    }

    return 0;
}
```

+ File led_driver.c
```c
/*
 * Module led driver, support control led param:
 *         - gpio  : gpio number used for control led
 *         - lable : discription of led device
 *         - mode  : control on, off or blink led
 *         - brightness: Bringhtness of led (0 - 100%)
 *         - blink_period: blink period use in flash mode
 * Driver expose attribute to sysfs as path /sys/led_driver/...
 */

#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/gpio.h>
#include <linux/timer.h>    /* Using kernel timer for blink led*/

MODULE_LICENSE("GPL");
MODULE_AUTHOR("HuLaTho");
MODULE_DESCRIPTION("A simple Linux LED driver");
MODULE_VERSION("0.1");

static unsigned int gpio_num = 9;      /* gpio used for led control, GPIO1_IO9*/
static char* lable = "My led driver";   /* Desciption of LED*/
static unsigned int blink_period = 100; /* ms*/
static unsigned int brightness = 100;   /* 100% */
enum modes { OFF, ON, FLASH};           /* The available LED modes -- static not useful here*/
static enum modes mode = ON;            /* Default mode is ON*/

/* Define module param */
module_param(gpio_num, uint, S_IRUGO);
MODULE_PARM_DESC(gpio_num, " GPIO Button number (default=28)");

module_param(lable, charp, 0000);
MODULE_PARM_DESC(lable, "Lable of led driver");

module_param(blink_period, uint, S_IRUGO);
MODULE_PARM_DESC(blink_period, " Blink preriod in ms (min = 1, default=100, max = 1000)");

module_param(brightness, uint, S_IRUGO);
MODULE_PARM_DESC(brightness, " Led brightness (default=100 %)");

/**
 * Define attributes expose to sysfs
 *         - mode
 *         - perioid
 *         - brightness
 *         - lable
 */

/* Callback function to show or store LED mode */
static ssize_t mode_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf);
static ssize_t mode_store(struct kobject *kobj, struct kobj_attribute *attr, \
                          const char *buf, size_t count);

/* Callback function to show or store LED period */
static ssize_t period_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf);
static ssize_t period_store(struct kobject *kobj, struct kobj_attribute *attr, \
                            const char *buf, size_t count);

/* Callback function to show or store LED brightness */
static ssize_t brightness_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf);
static ssize_t brightness_store(struct kobject * kobj, struct kobj_attribute * attr, \
                                const char *buf, size_t count);

/* Callback function to show or store LED lable */
static ssize_t lable_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf);


/* Define attribute for led module*/
static struct kobj_attribute mode_attr       = __ATTR(mode, 0660, mode_show, mode_store);
static struct kobj_attribute period_attr     = __ATTR(blink_period, 0660, period_show, period_store);
static struct kobj_attribute brightness_attr = __ATTR(brightness, 0660, brightness_show, brightness_store);
static struct kobj_attribute lable_attr      = __ATTR_RO(lable);

static struct attribute *led_attrs[] = {
	&mode_attr.attr,
	&period_attr.attr,
	&brightness_attr.attr,
	&lable_attr.attr,
	NULL,
};

static struct attribute_group attr_group = {
	.attrs = led_attrs,
};

static struct kobject *led_kobj;            /* The pointer to the kobject*/

/* Helper functions used to control LED */
static void led_gpio_init(unsigned int gpio_num);
static void led_gpio_free(unsigned int gpio_num);
static void led_blink(unsigned long data); /* Implement blink led by kernel timer*/
DEFINE_TIMER(blink_timer, led_blink, 0, 0);

static int __init led_driver_init(void)
{
	int ret; /* return value*/
	printk(KERN_INFO "LED driver: Initializing LED driver\n");

	/* Create kobject and add to sysfs path /sys/led_driver */
	led_kobj = kobject_create_and_add("led_driver", kernel_kobj->parent);
	if (led_kobj == NULL) {
			printk(KERN_ALERT "LED driver: failed to create kobject\n");
			return -ENOMEM;
	}
	/* Add attribute to sysfs path /sys/led_driver/*/
	ret  = sysfs_create_group(led_kobj, &attr_group);
	if (ret) {
			printk(KERN_ALERT "LED driver: failed to create kobject\n");
			kobject_put(led_kobj);
			return ret;
	}

	/* Init and register led gpio for control LED */
	led_gpio_init(gpio_num);

	return 0;
}

static void __exit led_driver_exit(void)
{
	kobject_put(led_kobj);
	led_gpio_free(gpio_num);
	printk(KERN_ALERT "LED driver: module exit!");
}

module_init(led_driver_init);
module_exit(led_driver_exit);

/* Callback function to show or store LED mode */
static ssize_t mode_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf)
{
	printk(KERN_INFO "LED driver: %s\n", __func__);

	switch (mode) {
	case OFF:   return sprintf(buf, "off\n");
	case ON:    return sprintf(buf, "on\n");
	case FLASH: return sprintf(buf, "flash\n");
	default:    return sprintf(buf, "LKM Error\n"); // Cannot get here
	}
}
static ssize_t mode_store(struct kobject *kobj, struct kobj_attribute *attr, \
                          const char *buf, size_t count)
{
	printk(KERN_INFO "LED driver: %s\n", __func__);

	/* Get mode*/
	if (strncmp(buf, "on", count - 1) == 0) {
			mode = ON;
			gpio_set_value(gpio_num, 1);
	} else if (strncmp(buf, "off", count - 1) == 0) {
			mode = OFF;
			gpio_set_value(gpio_num, 0);
	} else if (strncmp(buf, "flash", count - 1) == 0) {
			mode = FLASH;
			mod_timer(&blink_timer, jiffies + blink_period / 2 * HZ / 1000);
	}
	return count;
}

/* Callback function to show or store LED period */
static ssize_t period_show(struct kobject *kobj, struct kobj_attribute *attr, \
                           char *buf)
{
	printk(KERN_INFO "LED driver: %s\n", __func__);
	return sprintf(buf, "%d\n", blink_period);
}
static ssize_t period_store(struct kobject *kobj, struct kobj_attribute *attr, \
                            const char *buf, size_t count)
{
	int value;
	printk(KERN_INFO "LED driver: %s\n", __func__);
	sscanf(buf, "%d", &value);
	if (value >= 0 && value <= 1000) {
			printk(KERN_INFO "LED driver: set preriod to %d", value);
			blink_period = value;
	}
	return count;
}

/* Callback function to show or store LED brightness */
static ssize_t brightness_show(struct kobject *kobj, struct kobj_attribute *attr, \
                               char *buf)
{
	printk(KERN_INFO "LED driver: %s\n", __func__);
	return sprintf(buf, "%d\n", brightness);
}

static ssize_t brightness_store(struct kobject * kobj, struct kobj_attribute * attr, \
                                const char *buf, size_t count)
{
	int value;
	printk(KERN_INFO "LED driver: %s\n", __func__);
	sscanf(buf, "%d", &value);
	if (value >= 0 && value <= 100) {
			printk(KERN_INFO "LED driver: set brightness to %d", value);
			brightness = value;
	}
	/*TODO: implement*/

	return count;
}

static ssize_t lable_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf)
{
	printk(KERN_INFO "LED driver: %s\n", __func__);
	return sprintf(buf, "%s\n", lable);
}

void led_gpio_init(unsigned int gpio_num)
{
	gpio_request(gpio_num, "my_led");
	gpio_direction_output(gpio_num, 1); /* Default led ON when load module*/
}

void led_gpio_free(unsigned int gpio_num)
{
	gpio_set_value(gpio_num, 0);
	gpio_free(gpio_num);
	del_timer(&blink_timer);
}

static void led_blink(unsigned long data)
{
	static int led_state = 0;
	led_state = !led_state;
	gpio_set_value(gpio_num, led_state);
	if (mode == FLASH)
			mod_timer(&blink_timer, jiffies + blink_period * HZ / 1000);
	if (mode == ON)
			gpio_set_value(gpio_num, 1);
	if (mode == OFF)
			gpio_set_value(gpio_num, 0);
}
```

+ File Makefile
```Makefile
MODULE:= led_driver

obj-m += $(MODULE).o

KERNELDIR ?= /home/thonv12/yocto_imx/build-xwayland/tmp/work/mys_8mmx-poky-linux/linux-imx/5.4-r0/build

all:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules
clean:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) clean
install:
	scp $(MODULE).ko root@192.168.1.115:
```

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
