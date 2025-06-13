// eureka-client.js
const { Eureka } = require('eureka-js-client');
const cli = require("cli-color");
const Logger = require('./logger');

const client = new Eureka({
  instance: {
    app: 'inventory-service',
    instanceId: 'inventory-service-1',
    hostName: 'localhost',
    ipAddr: '127.0.0.1',
    statusPageUrl: 'http://localhost:3000/info',
    port: {
      '$': process.env.port || 8001,
      '@enabled': true,
    },
    vipAddress: 'inventory-service',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: 'localhost',
    port: 8761,
    servicePath: '/eureka/apps/',
  },
});

// Start the client
client.start(error => {
  if (error) {
    Logger.error(`Eureka registration failed: ${error}`);
  } else {
    Logger.info("Service registered with Eureka");
  }
});