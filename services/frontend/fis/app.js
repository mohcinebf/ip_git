// // Requiring module
const express = require('express');

// // Creating express object
// const app = express();

// // Handling GET request
// app.get('/', (req, res) => {
// 	res.send('A simple Node App is '
// 		+ 'running on this server')
// 	res.end()
// })

// // Port Number
// const PORT = process.env.PORT ||5000;

// // Server Setup
// app.listen(PORT,console.log(
// `Server started on port ${PORT}`));
// // read object from html file

var app = require('express')();
app.use(express.static(__dirname));
var http = require('http').Server(app);
var io = require('socket.io')(http);


http.listen(80, function(){
    console.log('listening on *:80');
});
app.get('/', function(req, res){
    res.sendFile(__dirname + '/index.html');

});

io.on('connection',socket =>{
    socket.on('message',data =>{
        console.log("data"+data);
    })
    socket.emit('lineinfo',{name:'Linie 13',dest:'Lünberg - Wolfsburg' })
    socket.emit('nextstop',{name:'Herrmannplatz',planned_time:'12:00' })
    socket.emit('information',{type:'table',header:'haltstellen',message:{ headers:"",items:[['13:00','Aachen hbf'],['13:15','Normaluhr'],['13:30','Kaiserplatz'],['13:45','Hansemannplatz'],['14:00','Bushof']]}})
    //socket.emit('information',{type:'table',header:'anschluesse',message: {headers:['Zeit', 'Bus', 'Richtung'], items:[['13:05', '24', 'Hansemannplatz'],['13:10', '13', 'Kaiserplatz'],['13:15', '7','Normaluhr'],['13:30', '25','Kaiserplatz'],['13:45', '12','Hansemannplatz'],['14:00', '35','Bushof']]}})
    socket.emit('notification',{id:0, type:'add', message:'Umleitung ueber Duesseldorf Ring/ Duesseldorferstrasse gesperrt / AAchenerstrasse gesperrt'})
})