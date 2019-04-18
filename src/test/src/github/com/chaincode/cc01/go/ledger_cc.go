package main

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"strconv"
	"strings"
)

type LedgerChainCode struct {
}

var logger = shim.NewLogger("ledger_cc")

func (t *LedgerChainCode) Init(stub shim.ChaincodeStubInterface) peer.Response {
	_, args := stub.GetFunctionAndParameters()

	logger.Info(args)
	if len(args) != 2 {
		return shim.Error("Invalid args: " + strings.Join(args, "-"))
	}

	account := args[0]
	amount  := args[1]
	err := stub.PutState(account, []byte(amount))
	if err != nil {
		return shim.Error("putState occur error...")
	}
	return shim.Success([]byte("init is success..."))
}

func (t *LedgerChainCode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	function, args := stub.GetFunctionAndParameters()

	if function == "invoke" {
		// Make payment of X units from A to B
		return t.invoke(stub, args)
	} else if function == "query" {
		// the old "Query" is now implemtned in invoke
		return t.query(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"invoke\" \"query\"")
}

func (t *LedgerChainCode) invoke(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 3 {
		return shim.Error("Invalid args...")
	}

	payer  := args[0]
	payee  := args[1]
	amount := args[2]
	newAmount, _ := strconv.ParseInt(amount, 10, 64)
	payerBalance, err := stub.GetState(payer)
	if err != nil {
		return shim.Error("PutState occur error...")
	}
	payerBalanceInt, _ := strconv.ParseInt(string(payerBalance), 10, 64)
	if payerBalanceInt >= newAmount {
		payeeBalance, err := stub.GetState(payee)
		if err != nil {
			return shim.Error("GetState occur error...")
		}
		if payeeBalance == nil {
			stub.PutState(payee, []byte(strconv.FormatInt(newAmount, 10)))
		} else {
			payeeBalanceInt, _ := strconv.ParseInt(string(payeeBalance), 10, 64)
			newPayeeBalance := newAmount + payeeBalanceInt
			stub.PutState(payee, []byte(strconv.FormatInt(newPayeeBalance, 10)))
		}
		payerBalanceInt = payerBalanceInt - newAmount
		stub.PutState(payer, []byte(strconv.FormatInt(payerBalanceInt, 10)))
		return shim.Success([]byte("invoke is success..."))
	}
	return shim.Error("balance is not enough...")
}

func (t *LedgerChainCode) query(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Invalid args...")
	}

	balance, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error("GetState occur error...")
	}
	if balance == nil {
		return shim.Success([]byte("user not fount..."))
	}
	return shim.Success(balance)
}

func main() {
	err := shim.Start(new(LedgerChainCode))
	if err != nil {
		logger.Errorf("Error starting Ledger chaincode: %s", err)
	}
}


