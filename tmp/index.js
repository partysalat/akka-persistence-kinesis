const aws = require('aws-sdk');
const kinesis = new aws.Kinesis({
  region: 'eu-central-1'
});

const foo = Array.from(new Array(100))
  .forEach((bar, foo) => {
    const event = JSON.stringify({id: "" + (foo + 2), publish: true})
    console.log("Send ", event);
    kinesis.putRecord({
      Data: event,
      // Data: JSON.stringify("fkoepwfe"),
      PartitionKey: 'partiion_key_1',
      StreamName: 'myStream'
    }, function (err, data) {
      if (err) {
        console.error(err);
      }
    })
  });
;