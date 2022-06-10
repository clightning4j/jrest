<div align="center">
  <h1>:zap: JRest :zap:</h1>

  <img src="https://github.com/clightning4j/icons/raw/main/org/ic_launcher/res/mipmap-xxxhdpi/ic_launcher.png" />

  <p>
    <strong> :zap: JRest: A plugin for c-lightning to expose the API over rest :zap: </strong>
  </p>

  <p>
    <img alt="GitHub Workflow Status" src="https://img.shields.io/github/workflow/status/clightning4j/jrest/Integration%20testing?style=flat-square">
     <a href="https://github.com/clightning4j/JRPClightning/discussions">
      <img alt="GitHub Workflow Status" src="https://img.shields.io/badge/Discussion-Join-green">
     </a>
  </p>
</div>

## Table of Content

- Introduction
- Install
- Plugin parameter
- Example
- Who
- Support
- License

## Introduction

A Java plugin for c-lightning to expose the API over rest!

## Install

Java produces a jar and c-lightning needs a bash script to run it! 
The gradle script compiles the plugin and generate a bash script with the command `./gradlew createRunnableScript`

After the gradle process, you have the jar inside the `build/libs/lightning-rest.jar` and the script `lightning-rest-gen.sh` 
in the root directory of the project.

### Link the plugin in core lightning

You can run the plugin in a different way

You can insert the path of file `lightning-rest-gen.sh` inside the lightning conf file with the tag `plugin=YOUR_PATH` and run lightningd. In this case, you can insert the port that you want the server running with the propriety `lightningd --jrest-port=7000`.

In addition, you can run the plugin also dynamically from `lightning-cli plugin start YOUR_PATH` and the server runs on the port `7000` by default.

## Plugin parameter

- jrest-port: the port where you want to run the plugin
- jrest-on-startup: run the server at startup with core lightning

## Example


When you have installed the plugin, you can run it with the following command>

- `lightning-cli restserver start`: This command run the server and the caller
  will receive feedback like this:

```json
{
  "status": "running",
  "port": 7000
}
```

You can visit the documentation of server rest at link
[http://localhost:7000/ui](http://localhost:7000/ui) on your
browser

- `lightning-cli restserver stop`: This command stop the server and the caller
  will receive feedback like this:

```json
{
  "status": "stop",
  "port": 7000
}
```

## Who

[@vincenzopalazzo](https://github.com/vincenzopalazzo) is the developer of this
plugin and the motivation that he is developing this plugin is because he is
testing the [JRPCLightning](https://github.com/vincenzopalazzo/JRPClightning)
library.

All feedback are welcome :)

PS: The creator of this repository are searching a maintainer of this library,
if you like to work inside this repository to learn can write an email to
<vincenzopalazzodev@gmail.com>

## Support

TODO: for now look inside the [JRPCLightning](https://github.com/vincenzopalazzo/JRPClightning)

## License

<div align="center">
  <img src="https://opensource.org/files/osi_keyhole_300X300_90ppi_0.png" width="150" height="150"/>
</div>

```
MIT License

A Java plugin for c-lightning to expose the API over rest!
Copyright (c) 2020-21 Vincenzo Palazzo <vincenzopalazzodev@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

