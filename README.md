# 本文档通过AI生成，可能存在错误或不准确的地方，仅供参考。

# SimpleBlockChain

一个用Java实现的简单区块链系统，用于学习和演示区块链的基本概念。

## 项目简介

这是一个基于Java实现的简单区块链系统，实现了比特币的核心功能，包括：

- 区块链数据结构
- 挖矿机制
- 数字钱包
- 交易系统
- P2P网络通信
- 工作量证明 (PoW)

## 技术栈

- Java 8
- Maven
- Google Gson (用于JSON序列化)
- Bouncy Castle (用于加密)
- Lombok (简化代码)

## 主要功能模块

### 1. 比特币核心 (bitcoin/)
- BitcoinSystem: 区块链系统的核心实现
- Block: 区块数据结构
- Wallet: 数字钱包实现

### 2. 交易系统 (transaction/)
- Transaction: 交易实现
- TransactionInput: 交易输入
- TransactionOutput: 交易输出

### 3. 网络模块 (network/) 未完成
- P2PNetwork: P2P网络实现
- NetworkMessage: 网络通信消息

### 4. 客户端 (client/)
- BitcoinClient: 客户端实现
- CommandLineInterface: 命令行界面

### 5. 工具类 (utils/)
- Base58: Base58编码工具
- StringUtil: 通用工具类

## 快速开始

1. 克隆项目
```bash
git clone [项目地址]
```

2. 使用Maven编译
```bash
mvn clean install
```

3. 运行程序
```bash
java -jar target/SimpleBlockChain-1.0-SNAPSHOT.jar
```

## 核心特性

- 实现了基本的区块链数据结构
- 支持基于工作量证明(PoW)的挖矿机制
- 实现了数字签名和钱包功能
- 支持UTXO（未花费交易输出）模型
- 包含P2P网络功能，支持节点间通信
- 提供命令行界面进行交互

## 贡献指南

欢迎提交问题和改进建议！请提交issue或Pull Request。

## 许可证

MIT License
