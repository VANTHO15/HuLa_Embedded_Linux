# 💚 Platform Device Practice 💛

## 👉 Introduction and Summary

### 1️⃣ Introduction

+ Ở bài trước chúng ta đã tìm hiểu về platform device. Nếu các bạn chưa đọc thì xem link này nha [040_Platform_Device.md](../040_Platform_Device/040_Platform_Device.md). Ở bài này chúng ta sẽ thực hành thêm về platform device nhé.

### 2️⃣ Summary

Nội dung của bài viết gồm có những phần sau nhé 📢📢📢:
- [I. Introduction and Summary](#👉-introduction-and-summary)

    - [1. Introduction](#1️⃣-introduction)
    - [2. Summary](#2️⃣-summary)
- [II. Contents](#👉-contents)
    - [1. Thực hành platform device](#1️⃣-thực-hành-platform-device)
- [III. Conclusion](#✔️-conclusion)
- [IV. Exercise](#💯-exercise)
- [V. NOTE](#📺-note)
- [VI. Reference](#📌-reference)

## 👉 Contents

### 1️⃣ Platform driver
***Bài 1 Platform***
+ File platform_device_driver_sketup.c
```c
#include <linux/init.h>
#include <linux/module.h>
#include <linux/platform_device.h>
#include <linux/device.h>
#include <linux/delay.h>
#include <linux/slab.h>

#define DEVICE_NAME "my_dev"

static struct platform_device my_device = {
        .name               = DEVICE_NAME,
        .id                 = PLATFORM_DEVID_NONE,
};

void __init my_device_init_pdata(void)
{
        printk(KERN_ALERT " %s\n", __FUNCTION__);

        /* Register "my-platform-device" with the OS. */
        platform_device_register(&my_device);
}


/* Probe driver function called by the OS when the device with the name
 * "my-platform-device" is found in the system. */
static int my_driver_probe(struct platform_device *pdev)
{

        printk(KERN_ALERT " %s\n", __FUNCTION__);

        return 0;
}

static int my_driver_remove(struct platform_device *pdev)
{
        printk(KERN_ALERT " %s\n", __FUNCTION__);

        /* do something with the driver */

        return 0;
}


static struct platform_driver my_driver = {
        .probe      = my_driver_probe,
        .remove     = my_driver_remove,
        .driver     = {
                .name   = DEVICE_NAME,
                .owner  = THIS_MODULE,
        },
};

/* Entry point for loading this module */
static int __init my_driver_init_module(void)
{
        int ret;
        pr_info(" %s\n", __FUNCTION__);

        /* Add the device to the platform.
           This has to be done by the platform specific code.
           */
        my_device_init_pdata();

        /* We will use this when we know for sure that this device is not
           hot-pluggable and is sure to be present in the system.
           */
        ret = platform_driver_probe(&my_driver, my_driver_probe);
        // return driver_register(&my_driver);

        /* We will use this when we are not sure if this device is present in the
           system. The driver probe will be called by the OS if the device is
           present in the system.
           */
        //return platform_driver_register(&my_driver);
        return ret;
}

/* entry point for unloading the module */
static void __exit my_driver_cleanup_module(void)
{
        pr_info(" %s\n", __FUNCTION__);
        platform_driver_unregister(&my_driver);
        // driver_unregister(&my_driver);
}

module_init(my_driver_init_module);
module_exit(my_driver_cleanup_module);


MODULE_LICENSE("GPL");
MODULE_AUTHOR("HuLaTho");
```

+ File platform_device_driver.c
```c

#include <linux/init.h>
#include <linux/module.h>
#include <linux/platform_device.h>
#include <linux/device.h>
#include <linux/delay.h>
#include <linux/slab.h>

/* Data structure for the platform data of "my device" */
struct my_device_platform_data {
        int reset_gpio;
        int power_on_gpio;
        void (*power_on)(struct my_device_platform_data* ppdata);
        void (*power_off)(struct my_device_platform_data* ppdata);
        void (*reset)(struct my_device_platform_data* pdata);
};

/* Power on the device. */
static void my_device_power_on(struct my_device_platform_data* pdata)
{
        printk(KERN_ALERT " %s\n", __FUNCTION__);
}

/* Power Off the device. */
static void my_device_power_off(struct my_device_platform_data* pdata)
{
        printk(KERN_ALERT " %s\n", __FUNCTION__);
}

/* Reset the device. */
static void my_device_reset(struct my_device_platform_data* pdata)
{
        printk(KERN_ALERT " %s\n", __FUNCTION__);
}

/* "my device" platform data */
static struct my_device_platform_data my_device_pdata = {
        .reset_gpio         = 100,
        .power_on_gpio      = 101,
        .power_on           = my_device_power_on,
        .power_off          = my_device_power_off,
        .reset              = my_device_reset
};


static struct platform_device my_device = {
        .name               = "my-platform-device-dev",
        .id                 = PLATFORM_DEVID_NONE,
        .dev.platform_data  = &my_device_pdata
};

void __init my_device_init_pdata(void)
{
        printk(KERN_ALERT " %s\n", __FUNCTION__);

        /* Register "my-platform-device" with the OS. */
        platform_device_register(&my_device);
}

/***********************************************************************
 * Platform device driver code here......
 ***********************************************************************/

/* Device driver data structure. */
struct my_driver_data {
        int data1;
        int data2;
};

/* Suspend the device. */
static int my_device_pm_suspend(struct device *dev)
{
        struct my_driver_data* driver_data = dev_get_drvdata(dev);

        printk(KERN_ALERT " %s\n", __FUNCTION__);

        /* Suspend the device here*/

        return 0;
}

/* Resume the device. */
static int my_device_pm_resume(struct device *dev)
{
        struct my_driver_data* driver_data = dev_get_drvdata(dev);

        printk(KERN_ALERT " %s\n", __FUNCTION__);

        /* Resume the device here*/

        return 0;
}



/* Probe driver function called by the OS when the device with the name
 * "my-platform-device" is found in the system. */
static int my_driver_probe(struct platform_device *pdev)
{
        struct my_device_platform_data *my_device_pdata;
        struct my_driver_data* driver_data;

        printk(KERN_ALERT " %s\n", __FUNCTION__);

        my_device_pdata = dev_get_platdata(&pdev->dev);

        /* Power on the device. */
        if (my_device_pdata->power_on) {
                my_device_pdata->power_on(my_device_pdata);
        }

        /* wait for some time before we do the reset */
        mdelay(5);

        /* Reset the device. */
        if (my_device_pdata->reset) {
                my_device_pdata->reset(my_device_pdata);
        }

        /* Create the driver data here */
        driver_data = kzalloc(sizeof(struct my_driver_data), GFP_KERNEL);
        if (!driver_data)
                return -ENOMEM;

        /* Set this driver data in platform device structure */
        platform_set_drvdata(pdev, driver_data);

        return 0;
}

static int my_driver_remove(struct platform_device *pdev)
{
        struct my_device_platform_data *my_device_pdata = dev_get_platdata(&pdev->dev);;
        struct my_driver_data *driver_data = platform_get_drvdata(pdev);

        printk(KERN_ALERT " %s\n", __FUNCTION__);

        /* do something with the driver */

        // Power Off the device
        if (my_device_pdata->power_off) {
                my_device_pdata->power_off(my_device_pdata);
        }

        return 0;
}


static const struct dev_pm_ops my_device_pm_ops = {
        .suspend    = my_device_pm_suspend,
        .resume         = my_device_pm_resume,
};

static struct platform_driver my_driver = {
        .probe      = my_driver_probe,
        .remove     = my_driver_remove,
        .driver     = {
                .name   = "my-platform-device-dev",
                .owner  = THIS_MODULE,
                .pm     = &my_device_pm_ops,
        },
};

/* Entry point for loading this module */
static int __init my_driver_init_module(void)
{
        int ret;
        pr_info(" %s\n", __FUNCTION__);

        /* Add the device to the platform.
           This has to be done by the platform specific code.
           */
        my_device_init_pdata();

        /* We will use this when we know for sure that this device is not
           hot-pluggable and is sure to be present in the system.
           */
        ret = platform_driver_probe(&my_driver, my_driver_probe);
        // return driver_register(&my_driver);

        /* We will use this when we are not sure if this device is present in the
           system. The driver probe will be called by the OS if the device is
           present in the system.
           */
        //return platform_driver_register(&ineda_femac_driver);
        return ret;
}

/* entry point for unloading the module */
static void __exit my_driver_cleanup_module(void)
{
        pr_info(" %s\n", __FUNCTION__);
        platform_driver_unregister(&my_driver);
        // driver_unregister(&my_driver);
}

module_init(my_driver_init_module);
module_exit(my_driver_cleanup_module);

MODULE_AUTHOR("HuLaTho");
MODULE_LICENSE("GPL");
```

+ FIle Makefile
```Makefile
obj-m	+=  platform_device_driver_sketup.o
obj-m   +=  platform_device_driver.o

KERNELDIR ?= /lib/modules/$(shell uname -r)/build

PWD       := $(shell pwd)

all:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules
clean:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) clean
```


***Bài 2: platform_device_driver_binding_dtb***
+ File Makefile
```Makefile
obj-m   += platform_device_driver_bind_dtb.o

KERNELDIR ?= /lib/modules/$(shell uname -r)/build

PWD       := $(shell pwd)

all:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) modules
clean:
	$(MAKE) -C $(KERNELDIR) M=$(PWD) clean
```

+ File platform_device_driver_bind_dtb.c
```c
#include <linux/init.h>
#include <linux/module.h>
#include <linux/platform_device.h>
#include <linux/device.h>
#include <linux/delay.h>
#include <linux/slab.h>

#include <linux/of.h> /* "Open Firmware" */
#include <linux/of_address.h>

MODULE_LICENSE("GPL");
MODULE_AUTHOR("HuLaTho");

#define DEVICE_NAME "my_device"

static struct platform_device my_device = {
        .name               = DEVICE_NAME,
        .id                 = PLATFORM_DEVID_NONE,
};

void __init my_device_init_pdata(void)
{
        printk(KERN_ALERT " %s\n", __FUNCTION__);

        /* Register "my-platform-device" with the OS. */
        platform_device_register(&my_device);
}


/* Probe driver function called by the OS when the device with the name
 * "my-platform-device" is found in the system. */
static int my_driver_probe(struct platform_device *pdev)
{

        printk(KERN_ALERT " %s\n", __FUNCTION__);

        return 0;
}

static int my_driver_remove(struct platform_device *pdev)
{
        printk(KERN_ALERT " %s\n", __FUNCTION__);

        /* do something with the driver */

        return 0;
}

static struct of_device_id platform_device_ids[] = {
        {
                .compatible = "vender, my_device",
        }, { /* sentinel */ }
};

MODULE_DEVICE_TABLE(of, platform_device_ids);

static struct platform_driver my_driver = {
        .probe      = my_driver_probe,
        .remove     = my_driver_remove,
        .driver     = {
                .name   = DEVICE_NAME,
                .owner  = THIS_MODULE,
                .of_match_table = platform_device_ids,
        },
};

/* Entry point for loading this module */
static int __init my_driver_init_module(void)
{
        int ret;
        pr_info(" %s\n", __FUNCTION__);

        /* Add the device to the platform.
           This has to be done by the platform specific code.
           */
        my_device_init_pdata();

        /* We will use this when we know for sure that this device is not
           hot-pluggable and is sure to be present in the system.
           */
        ret = platform_driver_probe(&my_driver, my_driver_probe);
        // return driver_register(&my_driver);

        /* We will use this when we are not sure if this device is present in the
           system. The driver probe will be called by the OS if the device is
           present in the system.
           */
        //return platform_driver_register(&ineda_femac_driver);

        return ret;
}

/* entry point for unloading the module */
static void __exit my_driver_cleanup_module(void)
{
        pr_info(" %s\n", __FUNCTION__);
        platform_driver_unregister(&my_driver);
        // driver_unregister(&my_driver);
}

module_init(my_driver_init_module);
module_exit(my_driver_cleanup_module);
```

## ✔️ Conclusion
Ở bài này chúng ta đã biết về driver model. Tiếp theo chúng ta sẽ thực hành thêm về 123 nhé.


## 💯 Exercise
+ Thực hành theo bài viết

## 📺 NOTE
+ N/A

## 📌 Reference

[1] https://www.cs.columbia.edu/~sedwards/classes/2014/4840/device-drivers.pdf

[2] https://static.lwn.net/images/pdf/LDD3/ch14.pdf

[3] https://www.kernel.org/doc/Documentation/driver-model/

[4] https://bootlin.com/pub/conferences/2013/elce/petazzoni-device-tree-dummies/petazzoni-device-tree-dummies.pdf
