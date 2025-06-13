const mongoose = require("mongoose");
const Logger = require("./logger");

mongoose.connect(process.env.MONGO_URI).then(() => {
    Logger.info("MongoDb connected successfully");
}).catch((err)=>{
    Logger.error(`Error while connection to DataBase: ${err}`);
    
})