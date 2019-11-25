capitalystNgApp.controller( 'AccountHomeController', 
    function( $scope, $http, $ngConfirm, $window ) {
    
    // ---------------- Local variables --------------------------------------
    
    // ---------------- Scope variables --------------------------------------
    $scope.$parent.navBarTitle = "Accounts" ;
    $scope.accounts = null ;
    $scope.editScope = {
        index : -1,
        account : null
    } ;
    
    // -----------------------------------------------------------------------
    // --- [START] Controller initialization ---------------------------------
    console.log( "Loading AccountHomeController" ) ;
    initializeController() ;
    // --- [END] Controller initialization -----------------------------------
    
    
    // -----------------------------------------------------------------------
    // --- [START] Scope functions -------------------------------------------
    $scope.showNewAccountDialog = function() {
        broadcastEditScopeChanged( -1, {
            accountNumber: null,
            accountOwner: null,
            accountType: null,
            bankBranch: null,
            bankName: null,
            shortName: "",            
            description: "",
        }) ;
        $( '#editAccountDialog' ).modal( 'show' ) ;
    }
    
    $scope.editAccount = function( index ) {
        var clonedAccount = JSON.parse( JSON.stringify( $scope.accounts[index] ) ) ;
        broadcastEditScopeChanged( index, clonedAccount ) ;
        $( '#editAccountDialog' ).modal( 'show' ) ;
    }
    
    $scope.deleteAccount = function( index ) {
        console.log( "Deleting account at index = " + index ) ;
        var account = $scope.accounts[ index ] ;
        
        $ngConfirm({
            title: 'Confirm!',
            content: 'Delete account ' + account.shortName ,
            scope: $scope,
            buttons: {
                close: function(scope, button){
                    console.log( "User cancelled." ) ;
                },
                yes: {
                    text: 'Yes',
                    btnClass: 'btn-blue',
                    action: function(scope, button){
                        console.log( "Ok to delete account." ) ;
                        deleteAccount( account, function() {
                            $scope.accounts.splice( index, 1 ) ;
                        }) ;
                        return true ;
                    }
                }
            }
        });
    }
    
    $scope.viewLedger = function( account ) {
        $window.location.href = "/views/ledger/ledger.html?accountId=" + 
                                account.id ;
    }
    
    // --- [END] Scope functions

    // -----------------------------------------------------------------------
    // --- [START] Local functions -------------------------------------------
    
    function initializeController() {
        $scope.editScope = null ;
        fetchAccountSummaryListFromServer() ;
    }
    
    function broadcastEditScopeChanged( index, accountClone ) {
        $scope.editScope = {
            index : index,
            account : accountClone
        } ;
        $scope.$broadcast( 'editScopeChanged', null ) ;
    }
    
    // ------------------- Server comm functions -----------------------------
    function fetchAccountSummaryListFromServer() {
        
        $scope.$emit( 'interactingWithServer', { isStart : true } ) ;
        $http.get( '/Account' )
        .then ( 
            function( response ){
                $scope.accounts = response.data ;
            }, 
            function( error ){
                $scope.$parent.addErrorAlert( "Error fetch accounts." ) ;
            }
        )
        .finally(function() {
            $scope.$emit( 'interactingWithServer', { isStart : false } ) ;
        }) ;
    }

    function deleteAccount( account, successCallback ) {
        $scope.$emit( 'interactingWithServer', { isStart : true } ) ;
        $http.delete( '/Account/' + account.id )
        .then ( 
            function( response ){
                console.log( "Deleted account data" ) ;
                successCallback() ;
            }, 
            function( error ){
                $scope.$parent.addErrorAlert( "Error deleting account." ) ;
            }
        )
        .finally(function() {
            $scope.$emit( 'interactingWithServer', { isStart : false } ) ;
        }) ;
    }
} ) ;