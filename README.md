# Nuxeo Travel Expenses Sample

## About

This project allows to build the Marketplace package for the
`nuxeo-travel-expenses` sample addon.

## Building

To build and run the tests, simply start the Maven build:

    mvn clean install

To run functional tests:

    mvn clean install -Pftest

## Installing

To install the package:

 1. Take a fresh Nuxeo CAP (>= 7.2).

 2. Install the nuxeo-travel-expenses package:
      - From the AdminCenter (Upload + install)
      - From the command line using `nuxeoctl mp-install package.zip`
