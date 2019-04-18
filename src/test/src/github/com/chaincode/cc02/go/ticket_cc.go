package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"encoding/json"
	"fmt"
	"strings"
)

type TicketChainCode struct {
}
var logger = shim.NewLogger("ticket_cc")

func (t *TicketChainCode) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

func (t *TicketChainCode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	function, args := stub.GetFunctionAndParameters()
	logger.Info("invoke function: " + function+ " & args: " + strings.Join(args, "#"))
	switch function {
	case "upload":
		return t.upload(stub, args)
	case "obtain":
		return t.obtain(stub, args)
	}
	return shim.Error("Invalid invoke function name. Expecting \"upload\" \"obtain\"")

}

//{code:"",datetime:"",amount:"",operator:"",company:""}
func (t *TicketChainCode) upload(stub shim.ChaincodeStubInterface, args []string) peer.Response{

	var ticketStr string
	ticketStr = args[0]
	var ticketMap map[string]interface{}
	err := json.Unmarshal([]byte(ticketStr), &ticketMap)
	if err != nil {
		return shim.Error("Invalid upload param, the template like {code:\"\",datetime:\"\",amount:\"\",operator:\"\",company:\"\"}")
	}
	if len(ticketMap) != 5 {
		return shim.Error("Invalid upload param, the template like {code:\"\",datetime:\"\",amount:\"\",operator:\"\",company:\"\"}")
	}
	code, ok := ticketMap["code"]
	ticket, _ := stub.GetState(code.(string))
	if ticket != nil {
		return shim.Error("ticket has Existence!!")
	}
	if !ok {
		return shim.Error("ticket code is not fount!")
	}
	if _, ok := ticketMap["datetime"]; !ok {
		return shim.Error("ticket datetime is not fount!")
	}
	if _, ok := ticketMap["amount"]; !ok {
		return shim.Error("ticket amount is not fount!")
	}
	if _, ok := ticketMap["operator"]; !ok {
		return shim.Error("ticket operator is not fount!")
	}
	if _, ok := ticketMap["company"]; !ok {
		return shim.Error("ticket company is not fount!")
	}
	err = stub.PutState(code.(string), []byte(ticketStr))
	if err != nil {
		fmt.Printf("ticket put state error, error info %v\n", err)
		return shim.Error("ticket put state error!")
	}
	return shim.Success([]byte("upload success!"))

}

func (t *TicketChainCode) obtain(stub shim.ChaincodeStubInterface, args []string) peer.Response{
	if len(args) != 1 {
		return shim.Error("Invalid obtain param, expect length is 1")
	}
	code := args[0]
	ticket, err := stub.GetState(code)
	if err != nil {
		return shim.Error("ticket get state error!")
	}
	if ticket == nil {
		return shim.Error("ticket code is not fount!")
	}
	return shim.Success(ticket)
}

func main() {
	err := shim.Start(new(TicketChainCode))
	if err != nil {
		logger.Errorf("Error starting ticket chaincode: %s", err)
	}
}