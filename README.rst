web3j-cli: Web3j Command Line Tools
===================================

.. image:: https://api.travis-ci.org/web3j/web3j-docs.svg?branch=master
   :target: https://docs.web3j.io
   :alt: Documentation Status

.. image:: https://travis-ci.org/web3j/web3j-cli.svg?branch=master
   :target: https://travis-ci.org/web3j/web3j-cli
   :alt: Build Status

.. image:: https://codecov.io/gh/web3j/web3j-cli/branch/master/graph/badge.svg
   :target: https://codecov.io/gh/web3j/web3j-cli
   :alt: codecov

.. image:: https://badges.gitter.im/web3j/web3j.svg
   :target: https://gitter.im/web3j/web3j
   :alt: Join the chat at https://gitter.im/web3j/web3j

About
=====
The Web3j command line tools enable developers to interact with blockchains more easily. The Web3j command line tools allow allow you to use some of the key functionality of web3j from your terminal, including:

* New project creation
* Project creation from existing Solidity code
* Wallet creation
* Wallet password management
* Ether transfer from one wallet to another
* Generation of Solidity smart contract wrappers
* Smart contract auditing

**N.B.** For version prior to 4.5.7 go to web3j/web3j `releases <https://github.com/web3j/web3j/releases>`_

Installation
=====
On Linux/macOS, in a terminal, run the following command:

.. code-block:: bash

	installer=$(mktemp) &&
	curl --disable --proto '=https' --proto-redir '=https' --fail --show-error --silent --location https://get.web3j.io -o "$installer" &&
	sh "$installer" && rm -f "$installer"

This script will not work if Web3j has been installed using Homebrew on macOS.

On Windows, in PowerShell, run the following command:

.. code-block:: bash

	Set-ExecutionPolicy Bypass -Scope Process -Force; iex ((New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/web3j/web3j-installer/master/installer.ps1'))
   
Docs
=====

https://docs.web3j.io/latest/command_line_tools/


Credits
=====

Smart contract auditing functionality is provided by `SmartCheck <https://github.com/smartdec/smartcheck>`_
