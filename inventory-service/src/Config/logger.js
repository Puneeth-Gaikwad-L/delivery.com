const path = require('path');
const cli = require('cli-color')

class Logger {
  // Helper method to get the calling file automatically
  static getCallerFile() {
    const originalFunc = Error.prepareStackTrace;
    let callerfile;
    try {
      const err = new Error();
      Error.prepareStackTrace = (err, stack) => stack;
      const stack = err.stack;
      
      // Skip the first few stack frames:
      // 0: this function (getCallerFile)
      // 1: the Logger method (info/error/warn/debug)
      // 2: the actual caller we want
      const currentfile = stack[0].getFileName();
      
      // Find the first frame that's not this file
      for (let i = 1; i < stack.length; i++) {
        callerfile = stack[i].getFileName();
        if (currentfile !== callerfile) {
          break;
        }
      }
    } catch (e) {
      callerfile = 'unknown';
    }
    Error.prepareStackTrace = originalFunc;
    return callerfile || 'unknown';
  }
  static formatMessage(filename, message, level = 'INFO') {
    const timestamp = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const baseFilename = path.basename(filename);
    return `${timestamp} : ${baseFilename} : [${level}] ${message}`;
  }

  static info(message) {
    const filename = this.getCallerFile();
    console.log(cli.blueBright(this.formatMessage(filename, message, 'INFO')));
  }

  static error(message) {
    const filename = this.getCallerFile();
    console.error(cli.red(this.formatMessage(filename, message, 'ERROR')));
  }

  static warn(message) {
    const filename = this.getCallerFile();
    console.warn(cli.yellow(this.formatMessage(filename, message, 'WARN')));
  }

  static debug(message) {
    const filename = this.getCallerFile();
    console.log(this.formatMessage(filename, message, 'DEBUG'));
  }
}

module.exports = Logger;