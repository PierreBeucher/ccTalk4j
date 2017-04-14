ccTalk4j
========

ccTalk implementation for Java

# Getting started

ccTalk4 have 3 main components: `Device`, `DeviceHandler` and ccTalk `Message`.

Create and configure a `Device`.

```
String comPort = "COM5"; //COMX on Windows, /dev/ttyXYZ on Linux
byte address = 40; //validator default address is 40
BillValidator validator = DeviceFactory.billValidatorSerialCRC(comPort, address);
validator.connect();
```

Use the device through a `DeviceHandler`

```
BillValidatorHandler handler = new BillValidatorHandler(validator);

//add a listener which will handle ccTalk events
handler.addListener(new AbstractBillEventListener(){
  public void pendingCredit(BillValidatorHandler handler, BillEvent event, Bill bill) {
    //accept the incoming bill
    handler.getDevice().routeBill(BillValidator.ROUTE_CODE_SEND_BILL_CASHBOX_STACKER);
  }
  //implement other functions as wished
  /* ... */
  }
});
handler.initialise();
handler.startInputAcceptance();
Thread.sleep(60000); //lets accept bills for a minute 
handler.stopInputAcceptance();
handler.terminate();
```

Use the device directly with messages

```
MessageBuilder mb = new CRCChecksumMessageBuilder();
Message msg = mb.destination(address)
  .source(MASTER_ADDRESS)
  .header(Header.MODIFY_MASTER_INHIBIT_STATUS)
  .data(new byte[]{1})
  .build();
Message response = validator.requestResponse(msg);
```

# Detailed usage

You can use ccTalk4j through theses components:

- `handler.DeviceHandler` implementations provides a high-level interface to manipulate device: initialization/termination, input acceptance, event handling... Most of the time, you will want to use the handlers to manipulate your devices.
- `device.Device` implementations provides and interface to manipulate the device, mostly representing functional implementation of ccTalk headers.
- `MessagePort` implementations can write and read ccTalk messages directly through a serial port or any other port type.

## Device Handlers

Device handlers are the main component of ccTalk4j. They provide a human usable interface to manipulate devices, initialise and terminate devices (connection, check...), perform actions (accept input, request stuff...), etc.



### `BillValidatorHandler`

The `BillValidatorHandler` is used to manage a `BillValidator`, such as:
- Start and stop input accepting (updating the device inhibit status)
- Handle the event buffer through the `BillEventListener` interface
- Provide the `Bill` interface for incoming bills
- Perform all generic handlers actions

Most of the time, you'll need to use the following methods once device is initialised:
- acceptInputAcceptance() : change the inhibit status in order to accept bills
- stopInputAcceptance() : change the inhibit status in order to prevent bill insertion

See the Getting Started section for example usage, and Javadoc for details.

#### Handling bill events

The `BillEventListener` interface is used to handle bill events. Most of the time, you can use the `AbstractBillEventListener` to handle incoming events according to there types. 

You can register as much listener as you want through `addListener(BillEventListener)`, depending on the use you require.

```
BillValidatorHandler handler = new BillValidatorHandler(validator);
handler.addListener(new AbstractBillEventListener(){
  // called upon reception of certain bill event types 
  public void pendingCredit(BillValidatorHandler handler, BillEvent event, Bill bill) {}
  public void credit(BillValidatorHandler handler, BillEvent e, Bill bill) {}
  public void reject(BillValidatorHandler handler, BillEvent e) {}
  public void fraudAttempt(BillValidatorHandler handler, BillEvent e) {}
  public void status(BillValidatorHandler handler, BillEvent e) {}
  public void fatalError(BillValidatorHandler handler, BillEvent e) {}
	
  // called when the event buffer is read and an event loss is detected
  //i.e. the event counter differential is greater than the number of event that the device can keep in memory
  public void lostEvent(int lostEventCount, BillEventBuffer previousBuffer, BillEventBuffer newBuffer) {}
	
  //called when a credit or pendingCredit event is received, but the associated bill cannot be identified
  public void unknownBill(BillValidatorHandler handler, BillEvent e) {}
});
handler.initialise();
handler.startInputAcceptance();
```

#### Accepting, rejecting and extending bills ecrow time

When receiving a bill, the related event is fired. You can then accept, reject or extend the bill escrow timeout. You can then
call use the `BillValidator.routeBill()` method to handle the bill. For example, through an `AbstractBillEventListener` implementation:

```
public void pendingCredit(BillValidatorHandler handler, BillEvent event, Bill bill) throws CcTalkException {
  logger.info("Bill in escrow: {}. Accepting.", bill);
  handler.getDevice().routeBill(BillValidator.ROUTE_CODE_SEND_BILL_CASHBOX_STACKER);
  //handler.getDevice().routeBill(BillValidator.ROUTE_CODE_RETURN_BILL);
  //handler.getDevice().routeBill(BillValidator.ROUTE_CODE_EXTEND_ESCROW_TIMEOUT);
}
```

### Other handlers

Other handlers will be added in future releases.

## Devices 

Each ccTalk4j device implements the `Device` interface which provides a generic device interface such as connect/disconnect, specifying read/write timeout, performing simple poll.

### Available devices

Available devices are:
- `device.bill.validator.BillValidator` - representation of a bill validator

Coin acceptors and other devices will be added in further releases.

### Device lifecycle

The device lifecycle can be resumed in a few steps:
1. Initialisation: configure, connect and check. A device has to be configured and connected before use.
2. Use: use the device as you wish, accept input, poll, etc.
3. Termination: close connexion and terminate any pending element. Connexion must be closed otherwise it may remain active until the JVM exits, preventing other programs or components to use it.

Example using the `BillValidator` device:

```
//1. configure, connect and check
BillValidator validator = DeviceFactory.billValidatorSerialCRC(comPort, address);
validator.connect();
validator.simplePoll();

//2. Use the device as wished
validator.requestResponse(someMessage);
/* ... */

//3. Terminate the device: close connexion	
validator.disconnect();
```

### Device factories

The `device.DeviceFactory` class provides an API to create the following pre-configured devices:
- `BillValidator` standard message packets, CRC 16 checksum, serial communication
- *Not available yet* - `BillValidator` standard message packets, simple checksum, serial communication

### Requesting a device

Usually, a device can be request directly through its specialized interface, such as the `BillValidator.readBufferedNoteEvents()` methods. These methods scope
the common headers associated to device.

To send a message directly to a device, use the `request(Message)` `requestResponse(Message)` methods. Use a `utils.message.MessageBuilder` implementation
or build your own `core.Message` message using one of the available implementations.

#### Supported message types

Only CRC 16 Checksum messages available for now.

- CRC 16 Checksum message: fully available
- Standard message: not implemented yet
- Encrypted message: not implemented yet

# Internal architecture

Available soon.

# Releases

## 0.1.0-RC2

Minor improvements and corrections:
- The ROUTE_BILL response now accepts content length greater than one (this happens with the GBA ST2)
- Added possibility to clear all event listeners from a DeviceHandler
- Added initEventBufferQueue() on event handler to initialise event buffer with current event buffer from a device
- Added isTerminated method() on AbstractDeviceHandler

## 0.1.0-RC1

First pre-release with usable `BillValidator` and `BillValidatorHandler` and basic features.

Features:
- Basic implementation of ccTalk messaging. Support for CRC 16 checksum messaging on serial port.
- Basic implementation for a bill validator
- Basic implementation of the `BillValidatorHandler`

# Contacts

Pierre Beucher - beucher.pierre@gmail.com | pierre.beucher@semoa-group.com
