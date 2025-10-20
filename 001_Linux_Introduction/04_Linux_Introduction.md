# 💚 Phần 4 Linux Introduction 💛

## 👉 Introduction and Summary 
### 1️⃣ Introduction
Sau khi các bạn đã cài đặt phần mềm, đọc các bài viết ([01_Install_Tools](./00_Template.md), [02_SFTP_VScode](./02_SFTP_VScode.md), [03_Command_and_Edit_File](./03_Command_and_Edit_File.md)) thì chúng ta cũng đã quen với giao diện và có thể thao tác cơ bản trong ubuntu rồi. Tiếp theo chúng ta sẽ đến với bài chính ***Linux Introduction*** nhé 😉. Ở bài này chúng ta sẽ làm quen với các khái niệm và phần tử chính trong lập trình linux BSP (Board Support Package).
### 2️⃣ Summary
Nội dung của bài viết gồm có những phần sau nhé 📢📢📢:
- [I. Introduction and Summary](#👉-introduction-and-summary)
    - [1. Introduction](#1️⃣-introduction)
    - [2. Summary](#2️⃣-summary)
- [II. Contents](#👉-contents)
    - [1. Khái niệm freedom](#1️⃣-khái-niệm-freedom)
    - [2. OS là gì?](#2️⃣-os-là-gì?)
    - [3. Họ Unix](#3️⃣-họ-unix)
    - [4. Kiến trúc OS họ Unix](#4️⃣-kiến-trúc-os-họ-unix)
    - [5. Phân biệt chương trình và tiến trình](#5️⃣-phân-biệt-chương-trình-và-tiến-trình)
    - [6. Đa nhiệm](#6️⃣-đa-nhiệm)
    - [7. Các loại core Arm và công việc hay làm](#7️⃣-các-loại-core-arm-và-công-việc-hay-làm)
    - [8. Bộ BSP của Embedded Linux](#8️⃣-bộ-bsp-của-embedded-linux)
    - [9. Chạy chương trình c](#9️⃣-chạy-chương-trình-c)
- [III. Conclusion](#✔️-conclusion)
- [IV. Exercise](#💯-exercise)
- [V. NOTE](#📺-note)
- [VI. Reference](#📌-reference)

## 👉 Contents
### 1️⃣ Khái niệm freedom
Đầu tiên chúng ta cùng tìm hiểu về khái niệm freedom nhé. Một program được xem là free khi license của program đó cung cấp cho tất cả user 4 freedom (quyền tự do) sau:
- Quyền tự do chạy program đó cho bất kì mục đích nào.
- Quyền tự do nghiên cứu và thay đổi program.
- Quyền tự do redistribute lại các bản sao của program.
- Quyền tự do distribute các bản sao của các version đã được sửa đổi.

Những quyền tự do này được cung cấp cho cả mục đích thương mại và phi thương mại. Nghĩa là source code của program có thể được sửa đổi và distribute cho khách hàng.


### 2️⃣ OS là gì?

***Phần mềm máy tính:***

Phần mềm máy tính có thể chia ra làm 2 loại:
-  Các phần mềm hệ thống:  Dùng để quản lí hoạt động của bản thân máy tính
-  Các chương trình ứng dụng: Dùng để giải quyết các yêu cầu của người dùng

Mà phần quan trọng nhất của các chương trình hệ thống chính là OS. Chức năng cơ bản là kiểm soát tất cả nguồn tài nguyên, cung cấp các hàm chức năng, các dịch vụ hệ thống để trên đó các chương trình ứng dụng được viết ra sẽ sử dụng. Mô hình một máy tính như sau:

<img src="images/1.png" alt="HuLa" style="width:500px; height:auto;"/>   

Chúng ta có thể thấy trong OS sẽ là kernel, kernel hổ trợ OS thực hiện chức năng quản lí các thành phần sau đây:
+ 1.Thiết bị (devices): tạo một giao tiếp để các chương trình người dùng "nói chuyện" với thiết bị
+ 2.Bộ nhớ (memory): cấp bộ nhớ cho các chương trình (tiến trình) đang chạy
+ 3.Các Tiến trình (process): tạo, giám sát hoạt động của các tiến trình
+ 4.Liên lạc (communication) giữa các Tiến Trình

Một máy tính có rất nhiều thứ như CPU(s), bộ nhớ, các thiết bị ngoại vi... tạo thành một hệ thống rất phức tạp, việc viết các chương trình để kết hợp chúng lại cho tối ưu là rất kì công và phức tạp. Nếu những điều này lại để cho user có thể đụng chạm được tới thì sẽ dẫn đến risk có thể xảy ra. Như vậy cần tách user ra khỏi sự phức tập của phần cứng. Cách làm sẽ là đặt layer hardware cuối cùng sau đó xếp chồng layer software lên. Layer software sẽ quản lý tất cả các phần của máy tính và trao các interface cho user để đi lập trình layer ứng dụng.

Từ đây ta sẽ có 2 chế độ như sau:
- Kernel mode hay supervisor mode: 
    + Chế độ này được hổ trợ bởi kiến trúc của CPU, và nó ngăn người dùng truy nhập vào phần cứng.
    + Kernel đề cập đến phần cốt yếu nhất của các chương trình hệ thống, nó kiểm soát các tệp, khởi động và cho chạy các chương trình ứng dụng, đồng thời phân chia thời gian sử dụng CPU cho các chương trình, cấp bộ nhớ cũng như các tài nguyên khác cho các chương trình của người dùng. Bản thân kernel không làm gì nhiều nhưng cung cấp các công cụ nguyên thuỷ (primitive functions) mà các tiện ích khác, các dịch vụ khác của OS được xây dựng
- User mode:  Các chương trình hệ thống, các trình ứng dụng mà sử dụng các dịch vụ của OS thì chạy ở chế dộ user mode. Tuy nhiên có sự khác biệt là các chương trình ứng dụng thì tận dụng những tiện ích hệ thống cho, còn các chương trình hệ thống là cần thiết để máy tính chạy được. Các chương trình ứng dụng chạy trong chế độ người dùng (user mode), các primitive functions chạy trong kernel . Việc kết nối giữa hai chế độ chạy này được thực hiện bởi gọi hệ thống (***system call***).


Tóm tắt các đặc điểm chính:
- OS là tên viết tắt của Operating System
- Là một chương trình quản lý tất cả những yếu tố như phần cứng và phần mềm của máy tính
- OS là chương trình đầu tiên được chạy, nó sở hữu và có toàn quyền quyết định đối với các thành phần khác cả hệ thống như Scheduling, memory management, file system,…
- Các chức năng chính của OS:
    + Là lớp vỏ bảo vệ cho hardware của hệ thống: Hiểu một cách đơn giản thì hardware của hệ thống giống như lòng trứng. Để tương tác trực tiếp với chúng thì người lập trình viên phải cẩn thận và hiểu rõ phần cứng. Tuy nhiên khi có hệ điều hành thì việc đó sẽ không cần thiết nữa. Hệ điều hành sẽ tạo ra lớp vỏ trứng bao quanh toàn bộ phần cứng. Lúc này lập trình viên thay vì tương tác với phần cứng thì sẽ tương tác với lớp vỏ là hệ điều hành, sau đó hệ điều hành sẽ là người làm việc với phần cứng
    + Là đối tượng duy nhất sở hữu, quản lý và phân phối phần cứng trong hệ thống: Khi hệ thống đi vào hoạt động sẽ có rất nhiều đối tượng tồn tại trong nó – Ví dụ như các chương trình Word, Excel, Chrome, … Và hệ điều hành cũng là một đối tượng nằm trong số đó. Tuy nhiên khác với các đối tượng còn lại, hệ điều hành là đối tượng được khởi động đầu tiên trong hệ thống, nó khởi tạo toàn bộ phần cứng và chiếm luôn quyền sở hữu chúng. Sau đó nó sẽ khởi tạo các đối tượng còn lại và quản lý, phân phối phần cứng cho toàn hệ thống
    + Cung cấp môi trường hoạt động và xử lý xung đột giữa các đối tượng: Do hệ điều hành là đối tượng đầu tiên được tạo ra trong hệ thống. Sau đó tất cả các đối tượng còn lại đều được sinh ra bởi hệ điều hành, do đó nó có toàn quyền điều khiển các đối tượng còn lại. Nó có thể sinh ra một đối tượng mới, tạm dừng một đối tượng đang chạy hoặc kết thúc vòng đời của chúng. Mỗi khi trong hệ thống xuất hiện trạng thái xung đột giữa các đối tượng thì hệ điều hành sẽ đứng ra phân xử và nó sẽ trực tiếp thi hành quyết định của mình. Tất cả các đối tượng còn lại đều phải tuân theo quyết định của nó

    <img src="images/Screenshot_34.png" alt="hello" style="width:500px; height:auto;"/>   

- Main parts
    + System call interface (SCI)​: Một layer mỏng cung cấp phương thức tương tác từ user space đến kernel space​
    + Process Management (PM)​: Create and destroy processes, Communication between different processes (kernel threads)​, Lập lịch CPU
    + Memory Management (MM)​: Quản lý bộ nhớ Physical sang bộ nhớ virtual, Memory allocation​, Swapping from memory to hard disk​
    + Virtual File System (VFS)​: Exports the common file interface​, Abstract file system functionality from implementation
    + File Systems​: Implementation of FS functionality​
    + Buffer Cache: Một tập hợp các hàm để thao tác bộ nhớ chính được thiết kế cho FS
    + Network Stack​: Implement the network protocols​, Phân phối các gói tin qua các chương trình và giao diện mạng
    + Device Drivers (DD)​: Tương tác với phần cứng
    + Arch: Architecture dependent code​
    
    <img src="images/Screenshot_35.png" alt="hello" style="width:500px; height:auto;"/>   

### 3️⃣ Họ Unix
- Ngày nay hệ điều hành đã trở lên quen thuộc với tất cả chúng ta. Tuy nhiên vào những năm 50 của thế kỷ trước, khi đó OS chưa ra đời, người ta phải nạp thẳng code vào máy tính. Mỗi máy tính tại một thời điểm chỉ chạy một chương trình và một chương trình sẽ phải điều khiển toàn bộ máy tính. Với máy tính tại thời điểm đó có kiến trúc đơn giản (không có chuột, bàn phím, màn hình, loa…) nên việc người lập trình viên quản lý toàn bộ máy tính bằng code của mình là khả thi. Tuy nhiên kiến trúc máy tính và yêu cầu tính toàn càng ngày càng phức tạp, do đó người ta cần đến một hệ thống có thể quản lý được máy tính và hỗ trợ nhiều nhất có thể đối với người lập trình viên. Từ yêu cầu thực tế đó hệ điều hành được ra đời
- Các hệ điều hành được ra đời sớm nhất có thể kể đến là GM-NAA I/O, BESYS, SOS, TENEX, Unix… Tuy nhiên thành công nhất chỉ có Unix, nó được thiết kế dựa trên rất nhiều các lý thuyết toán học. Do đó sau nửa thế kỷ trôi qua, phần thiết kế lõi của nó cũng không cần phải chỉnh sửa nhiều. Kiến trúc của Unix được áp dụng cho rất nhiều hệ điều hành phổ biến ngày nay như Android, Window, Linux, MACOS… Và chúng được gọi là các hệ điều hành họ Unix

### 4️⃣ Kiến trúc OS họ Unix
- Trên linux sẽ chia làm 2 không gian là User space và Kernel space, để nó thao tác với ổ cứng được thì phải gọi các hàm như open(), read() …, là hàm mà liên kết giữa 2 không gian trên. 
- Lớp application: Đây là lớp ngoài cùng của hệ điều hành, là nơi tương tác với user. Các tiến trình như word, excel... mà user sử dụng đều được chạy ở lớp này
- Lớp system call: Do cách thiết kế hệ điều hành không cho phép các ứng dụng từ tầng application được phép truy cập thẳng vào lớp kernel (Để tránh 1 lỗi trên tầng application có thể làm sập hệ thống). Nên họ đã thiết kế 1 lớp để ngăn cách gọi là lớp system call. Nhiệm vụ của lớp system call là cung cấp các đầu hàm (Ví dụ như read(), write()) cho lớp application sử dụng
- Lớp kernel: Đây là lớp trong cùng, nó bao ngoài phần cứng, quản lý và cung cấp những chức năng cơ bản của hệ điều hành như: Lập lịch, quản lý bộ nhớ, quản lý ngắt...


<img src="images/Screenshot_2.png" alt="hello" style="width:500px; height:auto;"/>    

<img src="images/Screenshot_27.png" alt="hello" style="width:500px; height:auto;"/>   

<img src="images/Screenshot_29.png" alt="hello" style="width:500px; height:auto;"/>  

<img src="images/Screenshot_31.png" alt="hello" style="width:500px; height:auto;"/>   

### 5️⃣ Phân biệt chương trình và tiến trình
- Chương trình: Là các file binary được build từ source code và nằm trên ổ cứng
- Tiến trình: Chúng là các chương trình đã được load vào hệ thống. Chúng bắt đầu sử dụng và tiêu thụ tài nguyên của hệ thống
    + Cũng giống như việc đặt tên để định danh cho con người, hệ điều hành sẽ đánh số cho từng tiến trình để định danh chúng. Số định danh đó sẽ là số thứ tự mà tiến trình đó được load vào hệ thống. Chúng được gọi là các process id. Hệ thống sẽ tương tác với các tiến trình thông qua định danh của chúng – process id
    + Input và output của tiến trình: Chúng là 2 file với file đầu là nơi tiến trình sẽ đọc dữ liệu đầu vào cho các hàm như scanf() và file thứ 2 sẽ là nơi tiến trình ghi kết quả đầu ra trong các hàm như printf(). Thông thường file input sẽ là bàn phím và file output sẽ là màn hình console

### 6️⃣ Đa nhiệm
- Hệ điều hành đa nhiệm: Cho phép chuyển đổi giữa các task vụ, gây cảm giác hệ thống có thể chạy song gain rất nhiều tiến trình
- Khả năng đa nhiệm của hệ điều hành Unix nhằm mục đích tạo cho người dùng cảm giác hệ thống đang xử lý nhiều công việc một lúc, đây cũng là một tính năng làm nên tên tuổi của Unix so với những đàn anh đi trước. Đối với con người 1 2ms là rất ngắn và không thể cảm nhận được, tuy nhiên đối với máy tính thì khoảng thời gian đó đủ để làm nhiều công việc khác. Do vậy hệ thống liên tục chuyển đổi giữa các công việc khác nhau nhưng vẫn đảm bảo khả năng xử lý tức thì cho người dùng.
- Lấy ví dụ một người dùng đang vừa soạn thảo văn bản vừa nghe nhạc. Chương trình soạn thảo văn bản cần 1 ms để xử lý mỗi khi người dùng gõ 1 phím bất kỳ. Chương trình nghe nhạc cần chạy định kỳ 1ms mỗi 1s. Nếu hệ thống không chuyển đổi giữa các task 1 cách hợp lý thì đôi khi chương trình nghe nhạc có thể bị gián đoạn do tại thời điểm đó CPU đang chạy chương trình soạn thảo văn bản. Tuy nhiên hệ điều hành sẽ không để cho điều đó xảy ra. Nó sẽ ưu tiên cho chương trình nghe nhạc được chạy đúng thời điểm, nếu tại thời điểm đó user ấn 1 phím bất kỳ, nó sẽ không xử lý luôn mà sẽ delay 1 2ms để chương trình nghe nhạc chạy xong rồi mới xử lý việc đó. Tuy nhiên việc delay 1 2ms trong soạn thảo văn bản sẽ không gây cảm giác xử lý trễ đối với người dùng và anh ta sẽ có giảm giác hệ thống chạy đồng thời cả chương trình nghe nhạc và soạn thảo văn bản.


### 7️⃣ Các loại core Arm và công việc hay làm
- Applications:
    + Lập trình ứng dụng người dùng
    + Tiêu thụ năng lượng lớn
    + Khả năng xử lý cao
- Realtime:
    + Ứng dụng có tính quan trọng về thời gian thực
- Micontroller:
    + Được ứng dụng rộng rãi cho các hệ thống smarthome, iot...
    + Tiết kiệm năng lượng

- Các công việc hay làm về Linux BSP
    + Bootloader: 
        + Tối ưu thời gian khởi động
        + Thêm lệnh điều khiển (U-Boot cmd)
        + Cấu hình phân vùng flash (NAND, eMMC)
        + Hỗ trợ secure boot nếu cần
        + ...
    + Linux kernel: 
        + Viết driver và test: i2c, spi, usb, can, gpio...
        + Tối ưu cấu hình kernel cho phần cứng
        + ...
    + Rootfs: 
        + Phát triển các ứng dụng tầng user space
        + Phát triển ứng dụng tầng trên: Qt, Python, C++
        + Tích hợp các dịch vụ: SSH, web server, MQTT...
        + ...
    + Porting
        + Hardware mới, kernel mới ...
        + Tùy chỉnh DTS (Device Tree)
        + Kiểm tra tương thích driver, peripheral
        + ...

### 8️⃣ Bộ BSP của Embedded Linux

<img src="images/Screenshot_25.png" alt="hello" style="width:500px; height:auto;"/>  

Mỗi dự án đều bắt đầu bằng việc thu thập, tùy chỉnh và triển khai bốn thành phần sau: toolchain, bootloader, kernel và root filesystem. Trong đó
- Toolchain: Là compiler và tools khác cần thiết để tạo code cho target device.
- Bootloader: Init board and loads the Linux kernel
- Kernel: Đây là trái tim của hệ thống. Kernel chứa các tiến trình và quản lý bộ nhớ, ngăn xếp mạng, trình điều khiển thiết bị và cung cấp dịch vụ cho các ứng dụng không gian người dùng.
- Root filesystem: Chứa các thư viện và chương trình được chạy sau khi kernel hoàn tất quá trình khởi tạo

<img src="images/Screenshot_9.png" alt="hello" style="width:500px; height:auto;"/>  

***a. Toolchain***
- Overview:
    + Toolchain là tập hợp của các tool để compile source code thành file executables và có thể run nó trên target device
    + Toolchain chứa a compiler, a linker, and runtime libraries
- Các thành phần của toolchain: Một standard GNU toolchain sẽ bao gồm 4 phần chính dưới đây
    + Binutils: Một tập hợp các binary utilities bao gồm assembler và linker (as, ld, objdump, objcopy...).
    + GNU Compiler Collection (GCC): Đây là các compiler cho C và các ngôn ngữ khác, tùy thuộc vào phiên bản GCC, bao gồm C++, Objective-C, Objective-C++, Java, Fortran, Ada và Go.
    + C library: Là các API được chuẩn hóa dựa trên POSIX. Nó là main interface cho OS kernel với application.
    + Debugger: The debugger is used to debug application. In the embedded Linux world, the typical debugger is GDB
- Types of toolchain: Có 2 type của toolchain như bên dưới:
    + Native: Toolchain này có thể được tìm thấy trong các Linux distribution, thường được compile trên x86, run trên x86 và generates code cho x86.
    + Cross: Toolchain này đã được xây dựng trên x86, nhưng chạy trên target architecture và generates code cho target architecture của ta (ARM, MIPS, PowerPC...)
    + Native Compile: Con X86 thì build ra nên chỉ chạy trên con X86 được thôi, con ARM build ra thì chỉ chạy được trên con ARM thôi
    + Cross Compile: là build trên con X86 nhưng có thể chạy trên con ARM
        +  Lý do là vì bộ source sẽ rất lớn mà build trên con ARM thì rất lâu nên cần 1 con X86 mạnh để build được bộ source và sau đó copy qua ARM và chạy
        + Thường thì trên ARM quá hạn chế về dung lượng lưu trữ và/hoặc bộ nhớ
        + Máy ARM rất chậm so với máy tính của chúng ta
        + Ta không muốn install all development tools trên ARM
    + Khi build yocto SDK cho IMX8MM thì ta sẽ có được toolchain là file .sh, khi chạy file .sh này ta sẽ có được môi trường compile cho IMX8MM và ta sẽ dùng cái này thay vì gcc để build 1 driver, khi này sẽ chạy được trên con IMX8MM

    <img src="images/Screenshot_10.png" alt="hello" style="width:500px; height:auto;"/>

    <img src="images/Screenshot_11.png" alt="hello" style="width:500px; height:auto;"/>

***b. Bootloader***

<img src="images/Screenshot_26.png" alt="hello" style="width:500px; height:auto;"/>

- Overview:
    + Sau khi bật nguồn hoặc reset, hệ thống ở trạng thái rất tối thiểu.
    + Bộ điều khiển DRAM chưa được thiết lập, do đó không thể truy cập bộ nhớ chính.
    + Tương tự, các giao diện khác chưa được cấu hình, do đó bộ nhớ được truy cập thông qua bộ điều khiển flash NAND, bộ điều khiển MMC, v.v. đều không khả dụng.
    + Các tài nguyên duy nhất hoạt động lúc đầu là lõi CPU, một số bộ nhớ tĩnh trên chip và ROM khởi động.
    + Chức năng chính của nó là khởi tạo phần cứng ở mức cơ bản và load các thành phần khác của OS (linux kernel, rootfs, device tree) lên RAM và trao quyền lại cho linux kernel.
        + Initialize all the low-level hardware details
        + Prepare the setup before chain loading any OS
        + Download and Check and OS binaries
        + Load an Operating System/runtime environment for the platform after self-tests
        + Jump to OS entry-point
    + Quá trình khởi động của một hệ thống nhúng có thể chia ra thành nhiều giai đoạn:
        + ROM code: Mã khởi động được ghi bởi nhà sản xuất, người dùng không thể thay đổi. Chức năng chính là setup hệ thống để load SPL vào Internal RAM
        + SPL: Chương trình tải phụ. Khởi tạo các thành phần cần thiết và load u-boot vào RAM
        + U-Boot: Load các thành phần của OS (Kernel, device tree, rootfs) vào RAM, truyền kernel parameters vào trao quyền điều khiển cho kernel.
        + Linux Kernel: Mount hệ thống file system (Roofs) và chạy tiến trình Init.

- ***Booting Sequence***:

    <img src="images/Screenshot_12.png" alt="hello" style="width:900px; height:auto;"/>

    + Boot Rom 
        + Khi hệ thống khởi động lần đầu tiên, hoặc reset thì quyền kiểm soát hệ thống sẽ thuộc về reset vector

        + Reset vector : nó là một đoạn mã assembly được ghi trước bởi nhà sản xuất chip (Manufaturer).

        + Sau đó reset vector sẽ trỏ tới địa chỉ vùng nhớ chứa các đoạn code khởi động đầu tiên, cụ thể là boot rom
            + Boot rom được nạp vào chip khi sản xuất, do đó mã ROM là độc quyền và không thể thay thế bằng mã nguồn mở tương đương.
            + Thông thường, ROM code không bao gồm code để initialize the memory controller, vì cấu hình DRAM rất cụ thể cho từng thiết bị, do đó nó chỉ có thể sử dụng Static Random Access Memory (SRAM), vốn không yêu cầu memory controller.
            + Trong SoC, khi SRAM không đủ lớn để load a full bootloader như U-Boot, cần phải có một bộ nạp trung gian gọi là Secondary Program Loader (SPL).
            + ROM code loads SPL tới SRAM

        + Nếu không có reset vector thì bộ xử lý sẽ không biết nên thực thi bắt đầu từ đâu.

        + Chức năng chính của boot rom đấy chính là sao chép nội dung trong file "MLO" (còn được gọi là Second Program Loader (SPL)) vào RAM và excute nó.

        + Do bộ nhớ của boot rom khá nhỏ nên rom code cũng được giới hạn ở việc khởi tạo một số phần cứng cần thiết cho việc load SPL lên hệ thống như: MMC/eMMC, SDcard, NAND flash. Các phần cứng này được gọi chung là boot device.

        + Rom code lựa chọn boot device (load từ thẻ nhớ, flash vv..) phụ thuộc vào việc cấu các pin thông qua switch/jump trên phần cứng.

        + Tóm lại: First code execute after reset, Located in a ROM on the SoC, Controls initial phase of boot process, Low level initialization, Performs different boot modes based on strap pins(RCONS settings) / fuses

        <img src="images/Screenshot_13.png" alt="hello" style="width:500px; height:auto;"/>

        <img src="images/Screenshot_14.png" alt="hello" style="width:500px; height:auto;"/>

        + Program Image - BootRom
            + IVT (Image Vector Table ) Header: Danh sách các con trỏ nằm tại một địa chỉ cố định mà ROM kiểm tra để xác định vị trí của các thành phần khác của Program Image: Entry Point (fixed offset), IVT Length, Version, Points to DCD table. Địa chỉ IVT thường nằm tại 0x1000 trong bộ nhớ QSPI Flash hoặc SD Card — nơi ROM bootloader sẽ tìm kiếm khi khởi động

            + DCD Table(Device Configuration Data): List of init commands

            + Boot Data: Bảng cho biết Program Image Location và kích thước của Program Image tính bằng byte

            + Secure CallBack Image: Địa chỉ tuyệt đối của Secure Callback Image. Được sử dụng để (authentication)xác thực Image.

        <img src="images/undefined.png" alt="hello" style="width:500px; height:auto;"/>


    + MLO - Second Program Loader (SPL):
        + Được store ở trong SD/eMMC, FLASH (NAND, NOR, HyperFlash)​
        + Low level initializations continued​
        + Đủ nhỏ để chạy từ internal RAM​
        + Can be updated​
        + Cấu hình hoặc tương tác người dùng rất hạn chế
        + Chủ yếu dùng để set-up boot process cho giai đoạn bootloader tiếp theo
        + Ngoài ra nó có thể làm điều sau:
            + SPL phải thiết lập memory controller và các thành phần thiết yếu khác của system để chuẩn bị loading Tertiary Program Loader (TPL - Uboot) vào DRAM.
            + Chức năng của SPL bị giới hạn bởi kích thước của SRAM
            + Ở cuối giai đoạn SPL này thì TPL-Uboot sẽ có mặt trong DRAM

        <img src="images/Screenshot_16.png" alt="hello" style="width:500px; height:auto;"/>

        + ARM Trusted Firmware Architecture
            + BL32 is OS OpTee
            + Optee: Giải mã cho các thằng có secure 
            + Arm trusted firmware: Chạy song song với linux mà không chết đi
            + Linux giao tiếp với BL31 qua PSCI và shared memory

            <img src="images/undefined (1).png" alt="hello" style="width:500px; height:auto;"/>

        + SPL - chương trình tải phụ. Nhiệm vụ chính của SPL đó chính là tiếp tục setup các thành phần cần thiết như DRAM controler, eMMC vv.. Sau đó load U-boot tới địa chỉ ***CONFIG_SYS_TEXT_BASE*** của RAM.

        + Chức năng chính của SPL là để load được U-boot lên RAM.

        <img src="images/Screenshot_15.png" alt="hello" style="width:500px; height:auto;"/>

    + U-Boot:
        + Giới thiệu uboot:
            + Open source firmware for hardware platforms​
            + Portable và dễ dàng debug (serial console output)
            + Hỗ trợ nhiều kiến ​​trúc: PPC, ARM, ARM64, MIPS, x86, m68k, NIOS, Microblaze, RISC-V
            + Được viết bằng ngôn ngữ c và assembly
            + Có thể khởi động nhiều hệ điều hành khác nhau: Linux, QNX, RTEMS, LynxOS, FreeBSD, NetBSD, VxWorks, WinCE
        + Thời điểm U-boot:
            + Tại thời điểm này, ta đang chạy một bộ nạp khởi động đầy đủ, đó chính là U-boot
            + Thông thường, có một command-line đơn giản cho phép ta thực hiện các tác vụ bảo trì, chẳng hạn như loading new boot and kernel images into flash storage, cũng như loading and booting a kernel, và có một cách để load the kernel automatically mà không cần sự can thiệp của user
        + Công dụng:
            + Đưa board về trạng thái stable sau khi reset
            + Loads OS image onto board and starts OS​
            + Web: https://docs.u-boot.org/en/latest/ 
        + Important command on Uboot​
            + Help: print online help​
            + printenv : Shows all variables​
            + printenv <variable-name> :Shows the value of a variable​
            + setenv <variable-name> <variable-value> :Changes the value of a variable or defines a new one, only in RAM
            + editenv <variable-name> :Edits the value of a variable in-place, only in RAM
            + saveenv: Lưu trạng thái hiện tại của môi trường vào bộ nhớ để duy trì
            + tftp: loads a file from the network to RAM
            + ping: to test the network​
            + bootd: (có thể viết tắt là boot), chạy lệnh default boot command, được lưu trữ trong environment variable bootcmd
            + bootz <address>: Starts a compressed kernel image loaded at the given address in RAM​
            + usb: để khởi tạo và điều khiển hệ thống con USB, chủ yếu được sử dụng cho các thiết bị lưu trữ USB như ổ cứng USB.
            + mmc: to initialize and control the MMC subsystem, used for SD and microSD cards
        + Summary
            + RomBoot: tries to find a valid SPL  from various storage sources, and load it into SRAM
            + U-Boot SPL: runs from SRAM. Initializes the DRAM, the NAND or SPI controller, and loads the secondary bootloader into DRAM and starts it. No user interaction possible
            + U-Boot: runs from DRAM. Initializes some other hardware devices (network, USB, etc.). Loads the kernel image from storage or network to DRAM and starts it. Shell with commands provided.​
            + Linux Kernel: runs from DRAM. Takes over the system completely (the bootloader no longer exists).

        <img src="images/Screenshot_20.png" alt="hello" style="width:500px; height:auto;"/>

        <img src="images/Screenshot_21.png" alt="hello" style="width:500px; height:auto;"/>

        + Board configuration defines trong IMX8MM Yocto

        <img src="images/Screenshot_22.png" alt="hello" style="width:900px; height:auto;"/>

        + Board Kconfig configuration ​trong IMX8MM Yocto

        <img src="images/Screenshot_23.png" alt="hello" style="width:900px; height:auto;"/>

        + U-Boot command  

        <img src="images/Screenshot_24.png" alt="hello" style="width:900px; height:auto;"/>

        + Sau khi được load vào RAM, u-boot sẽ thực hiện việc relocation. Di dời đến địa chỉ relocaddr của RAM (Thường là địa chỉ cuối của RAM) và nhảy đến mã của u-boot sau khi di dời.

        + Lúc này u-boot sẽ kiểm tra xem file uEnv.txt(Ở board BBB) có tồn tại hay không. Nếu có thực hiện load nó vào RAM ở bước tiếp theo

        + Bản thân uEnv.txt là một bootscript, nó định nghĩa các tham số cấu hình, kernel parameters. Các tham số này mặc định đã được cấu hình trong u-boot. Tuy nhiên chúng ta có thể thêm, sửa, xóa các cấu hình này thông qua file uEnv.txt. Việc load uEnv.txt là một sự tùy chọn (Optional), nghĩa là nó có thể có hoặc không.

        <img src="images/Screenshot_17.png" alt="hello" style="width:500px; height:auto;"/>

        + Tiếp theo u-boot sẽ tiếp tục load kernel, device tree vào RAM tại các địa chỉ mà đã được cấu hình từ trước ở trong mã nguồn u-boot hoặc trong file uEnv.txt. Sau cùng nó sẽ truyền toàn bộ kernel parameters và nhường quyền thực thi lại cho kernel.

        <img src="images/Screenshot_18.png" alt="hello" style="width:500px; height:auto;"/>

    + Kernel:
        + Sau khi nhận được quyền kiểm soát và các kernel parameters từ u-boot. Kernel sẽ thực hiện mount hệ thống file system (Rootfs) và cho chạy tiến trình Init trên RAM. Đây là tiến trình được chạy đầu tiên khi hệ thống khởi động thành công và chạy cho tới khi hệ thống kết thúc. Tiến trình Init sẽ khởi tạo toàn bộ các tiến trình con khác trên user space, các applications tương tác trực tiếp với người dùng. Lúc này, hệ thống của chúng ta đã hoàn toàn sẵn sàng cho việc sử dụng.

        <img src="images/Screenshot_19.png" alt="hello" style="width:500px; height:auto;"/>


    + Tại sao phải thực hiện Relocation?
        + Ở các giai đoạn trước của u-boot (ROM code or SPL). Chúng sẽ tải u-boot lên RAM mà không hề biết trước kế hoạch cho các vùng nhớ mà u-boot có thể tải lên là : bản thân u-boot, kernel-image, device tree, rootfs vv..

        + Nó đơn giản load u-boot lên RAM ở một địa chỉ thấp. Sau đó khi u-boot thực hiện một số khởi tạo cơ bản và phát hiện hiện tại nó không nằm ở vị trí được lập kế hoạch, chức năng relocation di chuyển u-boot đến vị trí đã lên kế hoạch và nhảy tới nó.

        + Bản chất việc relocation là để đảm bảo cho u-boot, kernel-image, device tree, rootfs vv.. khi load lên RAM sẽ không bị ghi đè lên nhau. Mà được load vào một vị trí tính toán từ trước.

***c. Kernel***
+ Giới thiệu:
    + KHÔNG có libC trong kernel
    + Kernel code supplies its own library implementations: String utilities, Cryptography, Compression
    + Không thể dùng các hàm thư viện C trong code kernel như: printf, memset, memcpy, malloc
    + Kernel cung cấp các hàm được tối ưu hóa cho từng kiến ​​trúc: printk, memset, kmalloc
    + Mã kernel có tính portable  cao => tất cả code bên ngoài arch/ đều có thể portable 
    + No floating point computation in kernel code​
    + API User space đến Kernel space không thay đổi (syscalls, /proc, /sys)
- Kernel có ba nhiệm vụ chính: quản lý tài nguyên, giao tiếp với phần cứng và cung cấp API mang lại mức độ trừu tượng cho các chương trình không gian người dùng
- Chức năng chính của Kernel:
    + Manage all the hardware resources: CPU, memory, I/O.​
    + Provide a set of portable, architecture and hardware independent APIs to allow user space applications and libraries to use the hardware resources
    + Xử lý việc truy cập đồng thời và sử dụng tài nguyên phần cứng từ các ứng dụng khác nhau.
- The main interface between the kernel and user space is the set of system calls​. About 400 system calls that provide the main kernel services


<img src="images/Screenshot_28.png" alt="hello" style="width:500px; height:auto;"/>

<img src="images/Screenshot_36.png" alt="hello" style="width:500px; height:auto;"/>

- LTP – Linux Test Project​
    + Được thiết kế để cải thiện Linux kernel bằng cách đưa automated testing vào kernel design​.
    + Trước LTP, chưa có môi trường kiểm thử chính thức nào dành cho các Linux developer.
    +  A test suite for Open Source community which validates the reliability, robustness and stability of Linux kernel​
    + LTP is not a performance benchmark​
    + http://ltp.sourceforge.net/ ​

- Phân biệt Monolithic vs μ-kernel​:
    + Monolithic Kernel (Nhân nguyên khối): 
        + Các trình ứng dụng chạy ở user mode khi thực hiện gọi một dịch vụ của Hệ thống, HĐH sẽ chuyển việc thực hiện dịch vụ vào kernel mode. Khi dịch vụ hoàn tất HĐH chuyển việc thực hiện chương trình đã phát sinh gọi dịch vụ trở lại user mode, chương trình này tiếp tục chạy. PC DOS là một ví dụ. Đặc điểm chung của loại này là kernel là một thực thể đơn, một chương trình rất lớn, mà các thành phần chức năng truy nhập tới tất cả các cấu trúc dữ liệu và thủ tục của hệ thống
        + Tất cả thành phần của hệ điều hành (quản lý tiến trình, bộ nhớ, hệ thống tập tin, driver thiết bị…) đều chạy trong kernel space.
        + Các thành phần có thể gọi trực tiếp lẫn nhau mà không cần thông qua cơ chế liên lạc trung gian.
        + Ưu điểm: Hiệu năng cao do không cần chuyển ngữ cảnh giữa user space và kernel space. Dễ chia sẻ tài nguyên giữa các thành phần.
        + Nhược điểm: Khó bảo trì vì thay đổi một phần có thể ảnh hưởng toàn bộ hệ thống, Ít an toàn hơn vì lỗi trong một driver có thể làm sập toàn bộ hệ thống
        + Dễ thiết kế nhưng Khó bảo trì và mở rộng
        + Ví dụ như Linux, BSD

        <img src="images/Screenshot_4.png" alt="hello" style="width:500px; height:auto;"/>   

    + μ-kernel
        + Chia OS ra thành nhiều tiến trình (TT), mỗi TT cung cấp một tập các dịch vụ ( ví dụ các dịch vụ bộ nhớ, dịch vụ tạo TT, dịch vụ lập biểu …). Các phần mềm dịch vụ (server) chạy trong user mode thực hiện vòng lặp để tiếp nhận yêu cầu các dịch vụ của nó từ các client. Client có thể là thành phần khác của HĐH, hay là một ứng dụng, yêu cầu phục vụ bằng cách gởi một thông điệp (message) tới server. Kernel của HĐH, là phần rất nhỏ gọn (microkernel) chạy trong kernel mode phát các thông điệp tới server, server thực hiện yêu cầu, kernel trả lại kết quả cho client. Server chạy các TT trong user mode tách biệt, nên nếu có sự cố (fail) thì toàn bộ hệ thống không hề bị ảnh hưởng. Với nhiều CPU, hay nhiều máy kết hợp, các dịch vụ chạy trên các CPU, máy khác nhau, thích hợp cho các tính toán phân tán
        + Chỉ giữ lại những chức năng tối thiểu trong kernel: quản lý tiến trình, bộ nhớ, IPC (inter-process communication).
        + Các thành phần khác như driver, hệ thống tập tin, giao tiếp mạng... chạy ở user space như các tiến trình riêng biệt.
        + Ưu điểm: Dễ thay thế, cập nhật từng phần mà không ảnh hưởng toàn hệ thống. Ổn định và an toàn hơn vì lỗi trong một module không làm sập kernel.
        + Nhược điểm: Hiệu năng thấp hơn do cần nhiều lần chuyển ngữ cảnh và IPC giữa các tiến trình. Phức tạp hơn khi triển khai và tối ưu
        + Ví dụ như QNX, MINIX, seL4, Symbian, Mac OS, WinNT

        <img src="images/2.png" alt="hello" style="width:500px; height:auto;"/>  

    <img src="images/Screenshot_32.png" alt="hello" style="width:500px; height:auto;"/>

- Request flow​

<img src="images/Screenshot_33.png" alt="hello" style="width:500px; height:auto;"/>

***d. Root Filesystem***
- Root Filesystem bao gồm một hệ thống phân cấp directory và file 
- Khi một filesystem được mounted trong một directory (gọi là mount point), nội dung của thư mục này sẽ phản ánh nội dung của thiết bị lưu trữ. When the filesystem is unmounted, the mount point is empty again. Điều này cho phép các ứng dụng dễ dàng truy cập tệp và thư mục, bất kể vị trí lưu trữ chính xác của chúng ở chỗ nào
- Location of the root filesystem can be mounted from different locations
    + From the partition of a hard disk​
    + From the partition of a USB key​
    + From the partition of an SD card​
    + From the partition of a NAND flash chip or similar type of storage device​
    + From the network, using the NFS protocol​

- Như windows cũng có file system, folder root của windows chính là My Computer và Desktop cũng là 1 thư mục con của My Computer.
- Tương tự như vậy, cấu trúc file system trong linux cũng vậy, bắt đầu từ thằng root và chứa các thằng khác bên trong.
- Các hệ điều hành trước Unix đã có hệ thống file system. Tuy nhiên đến lượt mình thì Unix nâng cấp chúng thêm một bậc nữa. Hệ thống sẽ coi toàn bộ các đối tượng tồn tại trong nó đều là file. Các đối tượng đó có thể là các hardware device, process, user… Từ đó hệ thống có thể quản lý tất cả các đối tượng thông qua một phương thức duy nhất đó là tương tác qua file
- Trong Unix file có thể hiểu như một định danh vì nhiều khi nó đại diện cho dữ liệu nằm trên ổ cứng hoặc một thiết bị nào đó. Ví dụ như các file đại diện cho từng process chúng nằm trong thư mục /proc/process_id. Mỗi một file sẽ có các thuộc tính ví dụ như kích thước, quyền sở hữu, ngày chỉnh sửa … Ngoài ra có 1 file dạng đặc biệt đó là thư mục. Thư mục là một file những dữ liệu bên trong nó chính là danh sách tên của các file nằm trong nó
- Việc tổ chức các file vào trong các thư mục và tạo các thư mục con trong thư mục cha nhằm phân cấp và sắp xếp hệ thống file người ta còn gọi chúng với cái tên cây thư mục. Cây thư mục có các node lá là file, node cành là các thư mục vào node gốc là thư mục root của hệ thống

- Folder Structure​

<img src="images/Screenshot_5.png" alt="hello" style="width:500px; height:auto;"/>  

<img src="images/Screenshot_30.png" alt="hello" style="width:500px; height:auto;"/>

### 9️⃣ Chạy chương trình c
***Khi có 1 file main.c***
```s
Câu lệnh: gcc –o name_file_output main.c
Sau khi chạy lệnh sẽ tạo ra 1 file name_file_output.
Chạy file đó là ra được kết quả: ./ name_file_output
```
<img src="images/Screenshot_6.png" alt="hello" style="width:500px; height:auto;"/>  

***Khi có nhiều file***
```s
Ta bỏ tất cả file .h vào folder include là được
gcc –o file_name_output main.c tho.c –I include/
```
<img src="images/Screenshot_7.png" alt="hello" style="width:500px; height:auto;"/>  

***Tất cả file .c và .h bỏ chung 1 folder***
```s
gcc –o file_name_output main.c tho.c –I. ( Dấu chấm cuối )
```

***4 giai đoạn biên dịch 1 chương trình C***
- Giai đoạn tiền xử lý(Pre-processing)
    + Loại boe comments.
    + Mở rộng các macros.
    + Mở rộng các file include.
    + Biên dịch các câu lệnh điều kiện.
    + Kết quả thu được là một file .i
	+ Chuyển từ ngôn ngữ bậc cao sang ngôn ngữ bậc thấp (Compilation)
- Biên dịch file .i thành file .s (assembly)
	+ Chuyển asm sang mã máy (Assembly)
- Output là file .o
	+ Giai đoạn linker
- Mỗi một file .o là một phần của chương trình, và ta sẽ liên kết lại để tạo một file hoàn chỉnh.

<img src="images/Screenshot_8.png" alt="hello" style="width:500px; height:auto;"/>  


## ✔️ Conclusion
Ở bài viết này hi vọng các bạn đã có 1 cái nhìn cơ bản về về linux BSP và quen được các khái niệm mà sau này sẽ gặp nhiều. Hãy đọc kĩ bài này là nó là nền tảng để tiếp tục đọc các bài tiếp theo.

## 💯 Exercise
Tất cả file .c và .h bỏ chung 1 folder, hãy viết câu lệnh để build ra file thực thi và chạy file thực thi đó ( .c .h tự viết )

## 📺 NOTE

<img src="images/image-10.png" alt="hello" style="width:900px; height:auto;"/>


## 📌 Reference

[1] https://bootlin.com/

[2] https://en.wikipedia.org/wiki/Das_U-Boot

[3] https://en.wikipedia.org/wiki/Booting

[4] https://www.bsdcan.org/2008/schedule/attachments/49_2008_uboot_freebsd.pdf

[5] https://www.slideshare.net/slideshow/uboot-startup-sequence/35290510

[6] https://www.slideshare.net/slideshow/wave-ubootppt/23703918

[7] https://wiki.tizen.org/w/images/6/62/3-Tizen_bootup(U-boot,Systemd).ppt

[8] https://ocw.cs.pub.ro/courses/so2

[9] https://wr.informatik.uni-hamburg.de/_media/teaching/wintersemester_2014_2015/pk1415-introduction.pdf

[10] Mastering Embedded Linux Programming (Third Edition) by Chris Simmonds and Frank Vasquez

[11] Introduction to Embedded Linux by Thomas Petazzoni ​

[12] Embedded Linux system development by Bootlin

[13] Luu Phu An

[14] https://viblo.asia/p/linux-boot-process-a-z-1Je5E6XLKnL

[15] Introduction to Linux - Machtelt Garrels