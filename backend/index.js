const express = require('express');
const request = require('request');
const app = express();
const port = 3000;

app.get('/balance/:addr', (req, res) => {

  // Extrapolate address, build call to ethereum node
  let address = req.params.addr;

  let options = {
    url: "http://localhost:3334", // port to GoEthereum JSONRPC server
    method: "post",
    body: JSON.stringify( {"jsonrpc":"2.0",
      "method":"eth_getBalance",
      "params":[address, "latest"],
      "id":1})
  };

  // Execute call, send response with 'balance' var
  request(options, (error, response, body) => {
      if (error) {
          console.error('An error has occurred: ', error);
      } else {
          var resp = {}
          resp.balance = body.result;
          res.statusCode(200);
          res.send(resp);
      }
  });
});

app.listen(port, () => {
  console.log(`API running at http://localhost:${port}`);
});
