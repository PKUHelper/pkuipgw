# pkuipgw

[![Build Status](https://api.travis-ci.org/PKUHelper/pkuipgw.svg?branch=master)](https://travis-ci.org/PKUHelper/pkuipgw) ![PullRequest](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)

A CLI (Command Line Interface) for PKU IP Gateway connection.

## Installation

Download from http://example.com/FIXME.

## Usage
```bash
$ pkuipgw [--help] [--version] <action> [<options>]
```

## Examples

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

Disconnect. No need for neither user id nor password to disconnect the current device from IP Gateway.
```bash
$ pkuipgw d
```
Disconnect all.
```bash
$ pkuipgw d -a -u 14000XXXXX -p ********
```

Disconnect the connection with specific ip.
```bash
$ pkuipgw d -u 14000XXXXX -p ******** --ip=192.168.0.1
```

Show the list of current connections.
```bash
$ pkuipgw l -u 14000XXXXX -p ********
```

## Document

#### Actions:

| Action Name | Command | Description |
| --- | --- | --- |
| connect | c, connect | Connect to PKU IP Gateway |
| disconnect | d, disconnect | Disconnect from PKU IP Gateway |
| list | l, list | Show the list of current connections |

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
