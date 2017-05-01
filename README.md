# pkuipgw

[![Build Status](https://api.travis-ci.org/PKUHelper/pkuipgw.svg?branch=master)](https://travis-ci.org/PKUHelper/pkuipgw) [![GitHub release](https://img.shields.io/github/release/PKUHelper/pkuipgw.svg)](https://github.com/PKUHelper/pkuipgw/releases/latest) [![License](https://img.shields.io/badge/License-EPL%201.0-blue.svg)](LICENSE) ![PullRequest](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)

A CLI (Command Line Interface) for PKU IP Gateway connection.

## Installation

To use pkuipgw, please make sure you have a JRE (Java Runtime Environment) on your computer.

To check whether your JRE is ready, run

```bash
$ java -version
```

You would see an output like the following if the JRE has been set successfully.

```
java version "1.8.0_112"
Java(TM) SE Runtime Environment (build 1.8.0_112-b16)
Java HotSpot(TM) 64-Bit Server VM (build 25.112-b16, mixed mode)
```

Download the latest released package of pkuipgw at: [![GitHub release](https://img.shields.io/github/release/PKUHelper/pkuipgw.svg)](https://github.com/PKUHelper/pkuipgw/releases/latest)
(click `pkuipgw-<latest-version>.tgz` to start downloading)

Unpack `pkuipgw-<latest-version>.tgz`

```bash
$ cd /path/to/the/directory/of/pkuipgw-<latest-version>.tgz
$ tar -zxvf pkuipgw-<latest-version>.tgz
```

Install

```bash
$ cd pkuipgw-<latest-version>
$ sudo install # or sudo ./install on MacOS
```

Enjoy yourself!

## Usage
```bash
$ pkuipgw [--help] [--version] <action> [<options>]
```

## Quick Start

Connect to IP Gateway.
```bash
$ pkuipgw c -u 14000XXXXX -p ********
```

You would see an output like the following one if the connection success.
```text
Connection success!
Status:
  connection_count  2
  balance           357.314
  ip                10.2.50.0
```

You could also use the full action command or full option. `--long-opt=ARG` is equivalent to `--long-opt "ARG"`. 
```bash
$ pkuipgw connect --user-id 14000XXXXX --password=********
```

Store the user id and password on local.
```bash
$ pkuipgw config -u 14000XXXXX -p ********
```

Once setup is completed, you would no longer need to input the user id or password.
```bash
$ pkuipgw c # no need to specify the user id or password now
```

Disconnect.
```bash
$ pkuipgw d
```
Disconnect all.
```bash
$ pkuipgw d -a
```

Disconnect the connection with specific ip.
```bash
$ pkuipgw d --ip=192.168.0.1
```
\[WARNING\] The server of PKU Computer Center does not validate the ip you request.
Passing a wrong ip may lead to weird output. 
We are not able to guarantee the output shows what exactly happens every time. 

Show the list of current connections.
```bash
$ pkuipgw l
```

## Document

#### Actions:

| Action Name | Command | Description |
| --- | --- | --- |
| connect | c, connect | Connect to PKU IP Gateway |
| disconnect | d, disconnect | Disconnect from PKU IP Gateway |
| list | l, list | Show the list of current connections |
| config | config | Set the global config |

#### Options:

| Option Name | Short Command | Long Command | Related Action | Description |
| --- | --- | --- | --- | --- |
| help | | --help | | Show help information |
| version | | --version | | Show version information |
| user-id | -u | --user-id | | Your IAAA account |
| password | -p | --password | | Your IAAA password |
| all | -a | --all | disconnect | To disconnect all connections |
| ip | | --ip | disconnect | Specify the IP to disconnect |

## Contributors

[@Luolc](https://github.com/Luolc)

## License

Copyright © 2017 PKU Helper

Distributed under the [Eclipse Public License version 1.0](LICENSE).
