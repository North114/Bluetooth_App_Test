###开发环境
* Windows7 64bits
* Android Studio 1.2.1.1

###程序简介
1. 程序主要用到了蓝牙连接和处理、Sqlite数据库和自定义View技术，实现了和集中器上的
蓝牙模块的通信，
2. 进入程序后是一个扫描蓝牙连接的Activity，在这里面打开蓝牙设备并扫描设备，扫描到
设备后，将设备名通过Listview显示出来，并在用户点击设备名后连接到该设备，进入主Activity
，主Activity中主要管理着3个Fragment，分别用来查询实时数据、查询历史数据和注册用户，
这3个Fragment共享主Activity的蓝牙连接，显示历史数据和实时数据部分用到了自定义View，通过
Listview显示这些自定义的View。
