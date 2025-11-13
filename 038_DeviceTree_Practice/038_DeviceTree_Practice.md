# 💚 Device Tree Practice 💛

## 👉 Introduction and Summary

### 1️⃣ Introduction

+ Ở bài trước chúng ta đã có lý thuyết về device tree. Nếu các bạn chưa đọc thì xem link này nha [037_DeviceTree.md](../037_DeviceTree/037_DeviceTree.md). Ở bài này chúng ta sẽ thực hành thêm về device tree nhé.

### 2️⃣ Summary

Nội dung của bài viết gồm có những phần sau nhé 📢📢📢:
- [I. Introduction and Summary](#👉-introduction-and-summary)

    - [1. Introduction](#1️⃣-introduction)
    - [2. Summary](#2️⃣-summary)
- [II. Contents](#👉-contents)
    - [1. Practice](#1️⃣-practice)
    - [2. Thực hành](#2️⃣-thực-hành)
- [III. Conclusion](#✔️-conclusion)
- [IV. Exercise](#💯-exercise)
- [V. NOTE](#📺-note)
- [VI. Reference](#📌-reference)

## 👉 Contents

### 1️⃣ Practice

***descriptor-based***
+ Descriptor base gpio thì sẽ được đại diện bởi 1 struct là gpio_desc như dưới
+ Còn integer base gpio thì sẽ là 1 số nguyên như bài hôm trước
+ Khi compatible của ta mà giống với 1 compatible trong device tree thì cái node trong device sẽ được driver của ta nhìn thấy luôn. Khi đó ta có thể sử dụng được 1 bộ hàm là gpio d. ( d là descriptor)
+ Thằng param *pdev chính là thằng node đã được tìm thấy

​<p align="center">
  <img src="Images/Screenshot_1.png" alt="hello" style="width:1000px; height:auto;"/>   
</p>

***Các API của GPIO D***
+ Thay vì dung GPIO thì có thể dùng bộ thư viện của linux cung cấp như bên trên
+ https://www.kernel.org/doc/Documentation/driver-model/devres.txt


​<p align="center">
  <img src="Images/Screenshot_2.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

+ Ví dụ như trong node của ta có 2 led là mled và aled, thì ta luôn loon có hậu tố phía sau là gpios. Còn khi gọi API thì ta lấy mình tiền tố và bỏ hậu tố đi, trong trường hợp này là lấy mình mled và aled
+ <&gpio0 31 : nghĩa là pin 31 đang thuộc blank 0
+ Chỗ status: phải là okay

​<p align="center">
  <img src="Images/Screenshot_3.png" alt="hello" style="width:500px; height:auto;"/>   
</p>

### 2️⃣ Thực hành
***Bài 1 Led Desciptor Based***
+ Mình sẽ tạo thêm file là hula.dtsi và trong file mys-imx8mm-evk.dts ta include file hula.dtsi vào
+ File led.c
```c
#include <linux/module.h>          /* This module defines functions such as module_init/module_exit */
#include <linux/platform_device.h> /* For platform device */
#include <linux/gpio/consumer.h>   /* For GPIO Descriptor */
#include <linux/of.h>              /* For Device Tree */

#define DRIVER_AUTHOR "thonv thonv@gmail.com"
#define DRIVER_DESC "LED blinking"

static struct gpio_desc *LED;

static const struct of_device_id gpiod_dt_ids[] = {
  {
      .compatible = "imx-led,dt",
  },
  {/* sentinel */}};

static int led_probe(struct platform_device *pdev)
{
    struct device *dev = &pdev->dev;

    LED = gpiod_get_index(dev, "mled", 0, GPIOD_OUT_HIGH);

    gpiod_set_value(LED, 1);

    pr_info("Hello! Driver probe successfully!\n");
    return 0;
}

static int led_remove(struct platform_device *pdev)
{
    gpiod_set_value(LED, 0);
    gpiod_put(LED);

    pr_info("Good bye probe!!!\n");
    return 0;
}

static struct platform_driver my_led_drv = {
    .probe = led_probe,
    .remove = led_remove,
    .driver = {
        .name = "imx-led,dt_sample",
        .of_match_table = of_match_ptr(gpiod_dt_ids),
        .owner = THIS_MODULE,
    },
};

module_platform_driver(my_led_drv);

MODULE_LICENSE("GPL");
MODULE_AUTHOR(DRIVER_AUTHOR);
MODULE_DESCRIPTION(DRIVER_DESC);
MODULE_VERSION("1.0");
```

+ Nội dung file dtsi
```xml
/ {	
	#address-cells = <1>;
	#size-cells = <1>;
	m_led {
		compatible = "imx-led,dt";
		mled-gpios = <&gpio5 4 GPIO_ACTIVE_HIGH>;
		status = "okay"; 
	};
};
```

+ File Makefile
```Makefile
KERNELDIR = /home/bv_rzvt/hula/imx-yocto-bsp/build-xwayland/tmp/work/mys_8mmx-poky-linux/linux-imx/5.4-r0/build

obj-m += led.o

all:
    make -C $(KERN_DIR) M=`pwd` modules

clean:
    make -C $(KERN_DIR) M=`pwd` modules clean
```

## ✔️ Conclusion
Ở bài này chúng ta đã biết về device tree. Tiếp theo chúng ta sẽ thực hành thêm về Device Tree nhé.


## 💯 Exercise
+ Thực hành theo bài viết

## 📺 NOTE
+ N/A

## 📌 Reference

[1] i.MX Linux Reference Manual

[2] Linux Device Drivers 3rd Edition (LDD3)

[3] https://events.static.linuxfound.org/sites/events/files/slides/petazzoni-device-tree-dummies.pdf

[4] David Gibson, Benjamin Herrenschmidt “Device Trees Everywhere”, OzLabs, 13 February2006, <http://www.ozlabs.com/~dgibson/home/papers/dtc-paper.pdf>
