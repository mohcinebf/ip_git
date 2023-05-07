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
    //socket.emit('information',{type:'table',header:'anschluesse',message: {headers:['Zeit', 'Bus', 'Richtung'], items:[['12:03', '13A', 'Kaiserplatz'], ['12:05', '24', 'Hansemannplatz']]}})
    socket.emit('information',{type:'table',header:'haltestellen',message: {headers:[], items:[['12:03', 'Kaiserplatz'], ['12:05', 'Hansemannplatz']]}})
    socket.emit('notification',{id:0, type:'add', message:'Umleitung ueber Duesseldorf Ring/ Duesseldorferstrasse gesperrt / AAchenerstrasse gesperrt'})
    //socket.emit('notification',{id:1, type:'add', message:'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. '})
})