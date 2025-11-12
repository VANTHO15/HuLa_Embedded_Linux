# 💚 SysFs Blynk Led IOREMAP💛

## 👉 Introduction and Summary

### 1️⃣ Introduction

+ Ở bài trước chúng ta đã biết cách tạo sysfs. Nếu các bạn chưa đọc thì xem link này nha [035_Create_SysFs.md](../035_Create_SysFs/035_Create_SysFs.md). Ở bài này chúng ta sẽ tìm hiểu về cách dùng sysfs để blynk led bằng ioremap nhé.

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
***Ta tạo ra 3 file là led.c led.h và Makefile***
+ File led.c
```c
/******************************************************************************
 *  @brief      GPIO sysfs driver (interger-based)
 *
 *  @author     thonv - thonv12@gmail.com
 *******************************************************************************/

#include <linux/module.h>  /* This module defines macro such as module_init/module_exit */
#include <linux/fs.h>      /* This module defines functions such as allocate major/minor number */
#include <linux/device.h>  /* This module defines functions such as class_create/device_create */
#include <linux/cdev.h>    /* This module defines functions such as kmalloc */
#include <linux/slab.h>    /* This module defines functions such as cdev_init/cdev_add */
#include <linux/uaccess.h> /* This module defines functions such as copy_to_user/copy_from_user */
#include <linux/string.h>  /* This module defines functions such as strncpy/strcmp */
#include <linux/gpio.h>    /* This module defines functions such as gpio_request/gpio_set_value */

#include <linux/io.h>		/* This module defines functions such as ioremap/iounmap */
#include "led.h"		    /* LED modules */

#define DRIVER_AUTHOR "hulatho"
#define DRIVER_DESC "GPIO sysfs driver (interger-based)"

uint32_t __iomem *base_addr;
uint32_t __iomem *base_addr_clk;
uint32_t __iomem *base_addr_mux_gpio1_io5;

static int32_t _value = 0;
static char _direct[8] = {};
static char _clock[8] = {};

struct m_foo_dev
{
    struct kobject *kobj_ref;
} mdev;


static int __init hello_world_init(void);
static void __exit hello_world_exit(void);

static ssize_t clock_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf);
static ssize_t clock_store(struct kobject *kobj, struct kobj_attribute *attr, const char *buf, size_t count);
static ssize_t value_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf);
static ssize_t value_store(struct kobject *kobj, struct kobj_attribute *attr, const char *buf, size_t count);
static ssize_t direction_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf);
static ssize_t direction_store(struct kobject *kobj, struct kobj_attribute *attr, const char *buf, size_t count);


struct kobj_attribute value = __ATTR(value, 0660, value_show, value_store);
struct kobj_attribute direction = __ATTR(direction, 0660, direction_show, direction_store);
struct kobj_attribute clock = __ATTR(clock, 0660, clock_show, clock_store);

static ssize_t clock_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf)
{
    return sprintf(buf, "%s\n", _clock);
}

static ssize_t clock_store(struct kobject *kobj, struct kobj_attribute *attr, const char *buf, size_t count)
{
	base_addr_clk = ioremap(CCM_CCGRn_ADDR_BASE, CCM_CCGRn_ADDR_SIZE);
	base_addr_mux_gpio1_io5 = ioremap(IOMUXC_SW_MUX_CTL_PAD_GPIO1_IO05_BASE, IOMUXC_SW_MUX_CTL_PAD_GPIO1_IO05_SIZE);

	if(!base_addr_clk)
		return -ENOMEM;
	if(!base_addr_mux_gpio1_io5)
		return -ENOMEM;

    if ( buf[0] == 'e' )
    {
        pr_info("ENABLE \n");
        *(base_addr_clk + GPIO1_ENABLE_CLOCK_OFFSET / 4) |= CLK;
	    *(base_addr_clk + GPIO1_SET_CLOCK_OFFSET / 4) |= CLK;

	    *(base_addr_mux_gpio1_io5) |= 1 << 4;  /* ENABLED SION */
	    *(base_addr_mux_gpio1_io5) &=~ 0x07;   /* Select signal GPIO1_IO05 */
    }
    else /* Disable */
    {   
        pr_info("DISABLE \n");
        *(base_addr_clk + GPIO1_ENABLE_CLOCK_OFFSET / 4) &=~ CLK;
	    *(base_addr_clk + GPIO1_SET_CLOCK_OFFSET / 4) &=~ CLK;
    }

    sscanf(buf, "%s", _clock);

    return count;
}

static ssize_t value_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf)
{
    return sprintf(buf, "%d\n", _value);
}

static ssize_t value_store(struct kobject *kobj, struct kobj_attribute *attr, const char *buf, size_t count)
{
    int32_t numb = 0;
    base_addr = ioremap(GPIO_1_ADDR_BASE, GPIO_1_ADDR_SIZE);

    /* Đưa về định dạng */
    sscanf(buf, "%d", &numb);
    switch (numb)
    {
        case 0: 
            /* Off */
            *(base_addr + GPIO_DR_OFFSET / 4) &=~ LED;
            break;
        case 1: 
            /* On */
            *(base_addr + GPIO_DR_OFFSET / 4) |= LED;
            break;

        default:
            return count;
    }

    sscanf(buf, "%d", &_value);

    return count;
}

static ssize_t direction_show(struct kobject *kobj, struct kobj_attribute *attr, char *buf)
{
    return sprintf(buf, "%s", _direct);
}

static ssize_t direction_store(struct kobject *kobj, struct kobj_attribute *attr, const char *buf, size_t count)
{
    base_addr = ioremap(GPIO_1_ADDR_BASE, GPIO_1_ADDR_SIZE);
	if(!base_addr)
		return -ENOMEM;

    if ( buf[0] == 'o' )
    {
        pr_info("OUT \n");
	    *(base_addr + GPIO_GDIR1_OFFSET / 4) |= LED;
    }
    else /* In */
    {   
        pr_info("IN \n");
        *(base_addr + GPIO_GDIR1_OFFSET / 4) &=~ LED;
    }

    sscanf(buf, "%s", _direct);

    return count;
}

/* Initialize group attrs */
static struct attribute *attrs[] = {
    &direction.attr,
    &value.attr,
    &clock.attr,
    NULL,
};

static struct attribute_group attr_group = {
    .attrs = attrs,
};

static int
    __init
    hello_world_init(void) /* Constructor */
{

    /* 01. It will create a directory under "/sys" , [firmware_kobj, class_kobj] */
    mdev.kobj_ref = kobject_create_and_add("duc_led", NULL);

    /* 02. Creating group sys entry under "/sys/ThoNV_LED/" */
    if (sysfs_create_group(mdev.kobj_ref, &attr_group))
    {
        pr_err("Cannot create group atrributes......\n");
        goto rm_kobj;
    }

    pr_info("Initizliaze successfully OK \n");
    return 0;

rm_kobj:
    kobject_put(mdev.kobj_ref);
    return -1;
}

static void __exit hello_world_exit(void) /* Destructor */
{
    sysfs_remove_group(mdev.kobj_ref, &attr_group); /* 02 */
    kobject_put(mdev.kobj_ref);                     /* 01 */

    pr_info("Good bye Led \n");
}

module_init(hello_world_init);
module_exit(hello_world_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR(DRIVER_AUTHOR);
MODULE_DESCRIPTION(DRIVER_DESC);
MODULE_VERSION("1.0");
```

+ File led.h
```h
#ifndef __GPIO__MODULE_H__
#define __GPIO__MODULE_H__

#define CCM_CCGRn_ADDR_BASE 0x30380000
#define GPIO_1_ADDR_BASE    0x30200000

#define GPIO1_ENABLE_CLOCK_OFFSET (0x40B0)
#define GPIO1_SET_CLOCK_OFFSET    (0x40B4)

#define GPIO_DR_OFFSET            (0x00)	    
#define GPIO_GDIR1_OFFSET	      (0x04)	/* 0 input ; 1 output */	

#define GPIO_1_ADDR_SIZE	(0x3020FFFF - GPIO_1_ADDR_BASE)
#define CCM_CCGRn_ADDR_SIZE (0x3038FFFF - CCM_CCGRn_ADDR_BASE)

#define IOMUXC_SW_MUX_CTL_PAD_GPIO1_IO05_BASE    0x3033003C
#define IOMUXC_SW_MUX_CTL_PAD_GPIO1_IO05_SIZE    (0x30330040 - 0x3033003C)

#define LED     ( 1 << 9 )   /* GPIO_1_IO9 LED 1*32 +5               D62-LED_GREEN*/
#define CLK     (0x00003333)

#endif  /* __LEDA_MODULE_H__ */
```

+ File Makefile
```Makefile
EXTRA_CFLAGS = -Wall

obj-m = led.o

KERNELDIR ?= /home/thonv12/yocto_imx/build-xwayland/tmp/work/mys_8mmx-poky-linux/linux-imx/5.4-r0/build

all:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules
	
clean:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) clean
 
```

## ✔️ Conclusion
Ở bài này chúng ta đã biết tạo ra SysFs. Tiếp theo chúng ta sẽ tìm hiểu về Device Tree nhé.


## 💯 Exercise
+ Thực hành theo bài viết

## 📺 NOTE
+ N/A

## 📌 Reference

[1] i.MX Linux Reference Manual

[2] Linux Device Drivers 3rd Edition (LDD3)

[3] L. R., Linux Kernel Development (Developer’s Library), 3rd ed. Addison-Wesley Professional, 2010.
