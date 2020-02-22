import { Controller } from '@nestjs/common';

const DbInteractor = require("models/DbInteractor");

@Controller('cosmos-db')
export class CosmosDbController {

   /**
    * Handles the various APIs for displaying and managing tasks
    * @param {DbInteractor} dbInteractor
    */
   dbInteractor : DbInteractor;
    
   constructor(dbInteractor) {
     this.dbInteractor = dbInteractor;
   }
   async showTasks(req, res) {
     const querySpec = {
       query: "SELECT * FROM root r WHERE r.completed=@completed",
       parameters: [
         {
           name: "@completed",
           value: false
         }
       ]
     };

     const items = await this.dbInteractor.find(querySpec);
     res.render("index", {
       title: "My ToDo List ",
       tasks: items
     });
   }

   async addTask(req, res) {
     const item = req.body;

     await this.dbInteractor.addItem(item);
     res.redirect("/");
   }

   async completeTask(req, res) {
     const completedTasks = Object.keys(req.body);
     const tasks = [];

     completedTasks.forEach(task => {
       tasks.push(this.dbInteractor.updateItem(task));
     });

     await Promise.all(tasks);

     res.redirect("/");
   }
 }

 module.exports = CosmosDbController;
