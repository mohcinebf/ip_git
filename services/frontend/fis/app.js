// // Requiring module
// const express = require('express');

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
    
})