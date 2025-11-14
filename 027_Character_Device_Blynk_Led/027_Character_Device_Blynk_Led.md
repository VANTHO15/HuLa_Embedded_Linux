# 💚 Character Device Driver Blynk Led 💛

## 👉 Introduction and Summary

### 1️⃣ Introduction

+ Ở bài trước chúng ta đã tạo ra được character device và sử dụng userspace để tương tác với character device. Nếu các bạn chưa đọc thì xem link này nha [026_Character_Device.md](../026_Character_Device/026_Character_Device.md). Ở bài này chúng ta sẽ sử dụng Character Device Driver ở mạch IMX8MM và thực hiện bài blynk led nhé.

### 2️⃣ Summary

Nội dung của bài viết gồm có những phần sau nhé 📢📢📢:
- [I. Introduction and Summary](#👉-introduction-and-summary)

    - [1. Introduction](#1️⃣-introduction)
    - [2. Summary](#2️⃣-summary)
- [II. Contents](#👉-contents)
    - [1. Thực hành](#1️⃣-thực-hành)
- [III. Conclusion](#✔️-conclusion)
- [IV. Exercise](#💯-exercise)
- [V. NOTE](#📺-note)
- [VI. Reference](#📌-reference)

## 👉 Contents

### 1️⃣ Thực hành
+ Ta áp dụng các kiến thức tạo 1 character device driver và thêm phần nháy led sử dụng ioremap vào là xong nhé. Về phần app trên user space thì truyền số 1 thì led on còn truyền số 0 thì led off nhé.
+ Bài thực hành này gồm 4 file là: led.c, led.h , Makefile, app.c

+ File led.c
```c
#include<linux/module.h>
#include<linux/fs.h>
#include<linux/cdev.h>
#include<linux/device.h>
#include<linux/kdev_t.h>
#include<linux/uaccess.h>
#include <linux/io.h>
#include "led.h"

uint32_t __iomem *base_addr;
uint32_t __iomem *base_addr_clk;
uint32_t __iomem *base_addr_mux_gpio1_io9;


#undef pr_fmt
#define pr_fmt(fmt) "%s : " fmt,__func__

#define DEV_MEM_SIZE 512

/* pseudo device's memory */
char device_buffer[DEV_MEM_SIZE];

/* This holds the device number */
dev_t device_number;

/* Cdev variable */
struct cdev thonv_cdev;

/*holds the class pointer */
struct class *class_thonv;

struct device *device_thonv;


loff_t thonv_lseek(struct file *filp, loff_t offset, int whence)
{
	loff_t temp;

	pr_info("lseek requested \n");
	pr_info("Current value of the file position = %lld\n",filp->f_pos);

	switch(whence)
	{
		case SEEK_SET:
			if((offset > DEV_MEM_SIZE) || (offset < 0))
				return -EINVAL;
			filp->f_pos = offset;
			break;
		case SEEK_CUR:
			temp = filp->f_pos + offset;
			if((temp > DEV_MEM_SIZE) || (temp < 0))
				return -EINVAL;
			filp->f_pos = temp;
			break;
		case SEEK_END:
			temp = DEV_MEM_SIZE + offset;
			if((temp > DEV_MEM_SIZE) || (temp < 0))
				return -EINVAL;
			filp->f_pos = temp;
			break;
		default:
			return -EINVAL;
	}
	
	pr_info("New value of the file position = %lld\n",filp->f_pos);

	return filp->f_pos;

}

ssize_t thonv_read(struct file *filp, char __user *buff, size_t count, loff_t *f_pos)
{
	pr_info("Read requested for %zu bytes \n",count);
	pr_info("Current file position = %lld\n",*f_pos);

	
	/* Adjust the 'count' */
	if((*f_pos + count) > DEV_MEM_SIZE)
		count = DEV_MEM_SIZE - *f_pos;

	/*copy to user */
	if(copy_to_user(buff,&device_buffer[*f_pos],count)){
		return -EFAULT;
	}

	/*update the current file postion */
	*f_pos += count;

	pr_info("Number of bytes successfully read = %zu\n",count);
	pr_info("Updated file position = %lld\n",*f_pos);

	/*Return number of bytes which have been successfully read */
	return count;
}

ssize_t thonv_write(struct file *filp, const char __user *buff, size_t count, loff_t *f_pos)
{
	pr_info("Write requested for %zu bytes\n",count);
	pr_info("Current file position = %lld\n",*f_pos);

	
	/* Adjust the 'count' */
	if((*f_pos + count) > DEV_MEM_SIZE)
		count = DEV_MEM_SIZE - *f_pos;

	if(!count){
		pr_err("No space left on the device \n");
		return -ENOMEM;
	}

	/*copy from user */
	if(copy_from_user(&device_buffer[*f_pos],buff,count)){
		return -EFAULT;
	}

	/*update the current file postion */
	*f_pos += count;

	pr_info("Number of bytes successfully written = %zu\n",count);
	pr_info("Updated file position = %lld\n",*f_pos);

	pr_info("Number of bytes successfully written = %s\n",device_buffer);

	/* set led*/
	switch (device_buffer[0])
    {
    case '1':
        pr_info("LED on\n");
        *(base_addr + GPIO_DR_OFFSET / 4) |= LED;
        break;

    case '0':
        pr_info("LED off\n");
        *(base_addr + GPIO_DR_OFFSET / 4) &=~ LED; 
        break;
    }

	/*Return number of bytes which have been successfully written */
	return count;
}

int thonv_open(struct inode *inode, struct file *filp)
{
	pr_info("open was successful\n");

	return 0;
}

int thonv_release(struct inode *inode, struct file *flip)
{
	pr_info("release was successful\n");

	return 0;
}


/* file operations of the driver */
struct file_operations thonv_fops=
{
	.open = thonv_open,
	.release = thonv_release,
	.read = thonv_read,
	.write = thonv_write,
	.llseek = thonv_lseek,
	.owner = THIS_MODULE
};


static int __init thonv_driver_init(void)
{
	int ret;

	/*1. Dynamically allocate a device number */
	ret = alloc_chrdev_region(&device_number,0,1,"thonv_devices");
	if(ret < 0){
		pr_err("Alloc chrdev failed\n");
		goto out;
	}

	pr_info("Device number <major>:<minor> = %d:%d\n",MAJOR(device_number),MINOR(device_number));

	/*2. Initialize the cdev structure with fops*/
	cdev_init(&thonv_cdev,&thonv_fops);

	/* 3. Register a device (cdev structure) with VFS */
	thonv_cdev.owner = THIS_MODULE;
	ret = cdev_add(&thonv_cdev,device_number,1);
	if(ret < 0){
		pr_err("Cdev add failed\n");
		goto unreg_chrdev;
	}

	/*4. create device class under /sys/class/ */
	class_thonv = class_create(THIS_MODULE,"thonv_class");
	if(IS_ERR(class_thonv)){
		pr_err("Class creation failed\n");
		ret = PTR_ERR(class_thonv);
		goto cdev_del;
	}

	/*5.  populate the sysfs with device information */
	device_thonv = device_create(class_thonv,NULL,device_number,NULL,"thonv");
	if(IS_ERR(device_thonv)){
		pr_err("Device create failed\n");
		ret = PTR_ERR(device_thonv);
		goto class_del;
	}

	base_addr = ioremap(GPIO_1_ADDR_BASE, GPIO_1_ADDR_SIZE);
	base_addr_clk = ioremap(CCM_CCGRn_ADDR_BASE, CCM_CCGRn_ADDR_SIZE);
	base_addr_mux_gpio1_io9 = ioremap(IOMUXC_SW_MUX_CTL_PAD_GPIO1_IO09_BASE, IOMUXC_SW_MUX_CTL_PAD_GPIO1_IO09_SIZE);

	*(base_addr_clk + GPIO1_ENABLE_CLOCK_OFFSET / 4) |= CLK;
	*(base_addr_clk + GPIO1_SET_CLOCK_OFFSET / 4) |= CLK;

	*(base_addr_mux_gpio1_io9) |= 1 << 4;  /* ENABLED SION */
	*(base_addr_mux_gpio1_io9) &=~ 0x07;  /* Select signal GPIO1_IO09 */

	*(base_addr + GPIO_GDIR1_OFFSET / 4) |= LED;

	pr_info("Module init was successful\n");

	return 0;

class_del:
	class_destroy(class_thonv);
cdev_del:
	cdev_del(&thonv_cdev);	
unreg_chrdev:
	unregister_chrdev_region(device_number,1);
out:
	pr_info("Module insertion failed\n");
	return ret;
}



static void __exit thonv_driver_cleanup(void)
{
	iounmap(base_addr);
	device_destroy(class_thonv,device_number);
	class_destroy(class_thonv);
	cdev_del(&thonv_cdev);
	unregister_chrdev_region(device_number,1);
	pr_info("module unloaded\n");
}


module_init(thonv_driver_init);
module_exit(thonv_driver_cleanup);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("hula");
MODULE_DESCRIPTION("character driver led");
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

+ File Makefile
```Makefile
KERNELDIR ?= /home/thonv12/yocto_imx/build-xwayland/tmp/work/mys_8mmx-poky-linux/linux-imx/5.4-r0/build

EXTRA_CFLAGS = -Wall
obj-m = led.o

all:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules
	
clean:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) clean
```

+ File app.c với file này ta copy qua board IMX8MM rồi build ra build app.exe nhé
```c
/******************************************************************************
 *  @brief      Userspace application to test the Device driver
 *
 *  @author     thonv - thonv12@gmail.com
 *******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
#include <stdio_ext.h>

#define CDEV_PATH "/dev/thonv"

int fd, option,of,length;
char write_buf[1024];
char read_buf[1024];

void printMenu()
{

    printf("****Please Enter the Option******\n");
    printf("        1. Write                 \n");
    printf("        2. Read                  \n");
    printf("        3. Lseek                  \n");
    printf("        4. Exit                  \n");
    printf("*********************************\n");
    printf(">>> ");
}

int main()
{
  printf("*********************************\n");
  printf("******Character device led*******\n");
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
          printf("length = ");
          scanf("%d", &length);
          printf("Data Reading ... ");
          read(fd, read_buf, length);
          printf("Done!\n");
          printf("Data: %s\n\n\n", read_buf);
          break;

      case 3:
          printf("enter offset from SEEK_SET = ");
          scanf("%d", &of);
          lseek(fd,of,SEEK_SET);
          break;

      case 4:
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

+ Chạy như bài trước rồi chạy file app.exe nhập 1 để bật led, nhâp 0 để tắt led

## ✔️ Conclusion
Ở bài này chúng ta đã tạo ra 1 Character Device Driver để bật tắt led. Tiếp theo chúng ta sẽ học về IOCLT và cũng áp dụng vào để blynk led luôn nhé.

## 💯 Exercise
+ Thực hành theo bài viết

## 📺 NOTE
+ N/A

## 📌 Reference

[1] i.MX Linux Reference Manual

[2] https://vimentor.com/vi/lesson/cap-phat-device-number

[3] https://vimentor.com/vi/lesson/cap-phat-dong-device-number-1
