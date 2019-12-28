function CatNode( name, parentNode ) {
    
    this.displayName = name ;
    this.parent = parentNode ;
    this.childNodes = [] ;
    this.depth = 0 ;
    this.selected = true ;
    
    this.addChild = function( cat ) {
        this.childNodes.push( cat ) ;
    }
    
    this.initialize = function() {
        var mother = parentNode ;
        while( mother != null ) {
            this.depth++ ;
            mother = mother.parent ;
        }
    }
    
    this.toggleSelection = function() {
        this.setSelection( this.selected ) ;
    }
    
    this.setSelection = function( state ) {
        this.selected = state ;
        for( var i = 0; i<this.childNodes.length; i++ ) {
            this.childNodes[i].setSelection( state ) ;
        }
    }
    
    this.initialize() ;
}

capitalystNgApp.controller( 'TxnPivotHomeController', 
    function( $scope, $http, $location ) {
    
    // ---------------- Local variables --------------------------------------
    
    // ---------------- Scope variables --------------------------------------
    $scope.$parent.navBarTitle = "Pivot of Transactions" ;
    $scope.catSelectionPaneHidden = false ;
    $scope.pivotDuration = {
        startDate : moment().subtract(1, 'month').startOf( 'month' ),
        endDate : moment().toDate(),
    } ;

    $scope.masterCategories = {
        credit : {
            l1Categories : [],
            l2Categories : new Map()
        },
        debit : {
            l1Categories : [],
            l2Categories : new Map()
        }
    } ;
    
    $scope.categoryTreeForDisplay = [
        new CatNode( 'Income', null ),
        new CatNode( 'Expense', null ),
    ] ;
    
    $scope.catLinearTree = [] ;

    // -----------------------------------------------------------------------
    // --- [START] Controller initialization ---------------------------------
    console.log( "Loading TxnPivotHomeController" ) ;
    initializeController() ;
    // --- [END] Controller initialization -----------------------------------
    
    
    // -----------------------------------------------------------------------
    // --- [START] Scope functions -------------------------------------------
    $scope.toggleCatSelectionPane = function() {
        var palette = document.getElementById( "category-selection-pane" ) ;
        var display = document.getElementById( "txn-pivot-panel" ) ;
        
        if( $scope.catSelectionPaneHidden ) {
            palette.style.display = "block" ;
            palette.style.width = "15%" ;
            display.style.width = "85%" ;
        }
        else {
            palette.style.display = "none" ;
            palette.style.width = "0%" ;
            display.style.width = "100%" ;
        }
        $scope.catSelectionPaneHidden = !$scope.catSelectionPaneHidden ;
    }
    
    // --- [END] Scope functions

    // -----------------------------------------------------------------------
    // --- [START] Local functions -------------------------------------------
    
    function initializeController() {
        initializeDateRange() ;
        fetchClassificationCategories() ;
    }
    
    function initializeDateRange() {

        var startDt = $scope.pivotDuration.startDate ;
        var endDt = $scope.pivotDuration.endDate ;
        var text = moment( startDt ).format( 'MMM D, YYYY' ) + ' - ' +
                   moment( endDt ).format( 'MMM D, YYYY' ) ;
        
        $('#pivotDuration span').html( text ) ;            
     
        $('#pivotDuration').daterangepicker({
            format          : 'MM/DD/YYYY',
            startDate       : startDt,
            endDate         : endDt,
            showDropdowns   : true,
            showWeekNumbers : false,
            opens           : 'right',
            drops           : 'down',
            buttonClasses   : ['btn', 'btn-sm'],
            applyClass      : 'btn-primary',
            cancelClass     : 'btn-default',
            separator       : ' to ',
            
            ranges : {
                'Last 2 Months' : [ 
                    moment().subtract(1, 'month').startOf( 'month' ), 
                    moment()
                ],
                'Last 3 Months' : [ 
                    moment().subtract(2, 'month').startOf( 'month' ), 
                    moment()
                ],
                'Last 6 Months' : [ 
                    moment().subtract(5, 'month').startOf( 'month' ), 
                    moment()
                ],
            },
            locale : {
                applyLabel       : 'Submit',
                cancelLabel      : 'Cancel',
                fromLabel        : 'From',
                toLabel          : 'To',
                customRangeLabel : 'Custom',
                daysOfWeek       : ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr','Sa'],
                firstDay         : 1,
                monthNames       : ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 
                                    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
            }
        }, 
        function( start, end, label ) {
            var text = start.format( 'MMM D, YYYY' ) + ' - ' + end.format('MMM D, YYYY') ;
            $('#pivotDuration span').html( text ) ;
            $scope.pivotDuration.startDate = start.toDate() ;
            $scope.pivotDuration.endDate   = end.toDate() ;
        });
    }
    
    // ------------------- Server comm functions -----------------------------
    function fetchClassificationCategories() {
        
        $scope.$emit( 'interactingWithServer', { isStart : true } ) ;
        $http.get( '/Ledger/Categories' )
        .then ( 
            function( response ){
                populateMasterCategories( response.data ) ;
                createCategoryTree( $scope.masterCategories.credit, 
                                           $scope.categoryTreeForDisplay[0] ) ;
                createCategoryTree( $scope.masterCategories.debit, 
                                           $scope.categoryTreeForDisplay[1] ) ;
                setTimeout( function(){
                    $( "#catTreeTable" ).treetable({ expandable: true }) ;
                    $( "#catTreeTable" ).treetable( 'expandNode', $scope.categoryTreeForDisplay[0].linearIndex ) ;
                    $( "#catTreeTable" ).treetable( 'expandNode', $scope.categoryTreeForDisplay[1].linearIndex ) ;
                }, 0 ) ;
            }, 
            function( error ){
                $scope.$parent.addErrorAlert( "Could not fetch classification categories." ) ;
            }
        )
        .finally(function() {
            $scope.$emit( 'interactingWithServer', { isStart : false } ) ;
        }) ;
    }
    
    function populateMasterCategories( categories ) {
        
        $scope.masterCategories.credit.l1Categories.length = 0 ;
        $scope.masterCategories.credit.l2Categories.clear() ;
        $scope.masterCategories.debit.l1Categories.length = 0 ;
        $scope.masterCategories.debit.l2Categories.clear() ;
        $scope.selectedL1Category = null ;
        $scope.selectedL2Category = null ;
        
        for( var i=0; i<categories.length; i++ ) {
            var category = categories[i] ;
            if( category.creditClassification ) {
                classifyCategoryInMasterList( 
                        $scope.masterCategories.credit.l1Categories, 
                        $scope.masterCategories.credit.l2Categories,
                        category ) ; 
            }
            else {
                classifyCategoryInMasterList( 
                        $scope.masterCategories.debit.l1Categories, 
                        $scope.masterCategories.debit.l2Categories,
                        category ) ; 
            }
        }
    }
    
    function classifyCategoryInMasterList( l1CatList, l2CatMap, category ) {
        
        var l1 = category.l1CatName ;
        var l2 = category.l2CatName ;
        
        if( l1CatList.indexOf( l1 ) == -1 ) {
            l1CatList.push( l1 ) ;
        }
        
        if( !l2CatMap.has( l1 ) ) {
            l2CatMap.set( l1, [] ) ;
        }
        
        var l2List = l2CatMap.get( l1 ) ;
        l2List.push( l2 ) ;
    }
    
    function createCategoryTree( masterCategoryCluster, rootDisplayCatNode ) {
        
        rootDisplayCatNode.linearIndex = $scope.catLinearTree.length + 1 ;
        $scope.catLinearTree.push( rootDisplayCatNode ) ;

        for( var i=0; i<masterCategoryCluster.l1Categories.length; i++ ) {
            var l1CatName = masterCategoryCluster.l1Categories[i] ;
            var l1Node = new CatNode( l1CatName, rootDisplayCatNode ) ;
            
            l1Node.linearIndex = $scope.catLinearTree.length + 1 ;
            $scope.catLinearTree.push( l1Node ) ;
            
            rootDisplayCatNode.addChild( l1Node ) ;
            var l2Nodes = masterCategoryCluster.l2Categories.get( l1CatName ) ;
            
            for( var j=0; j<l2Nodes.length; j++ ) {
                var l2NodeName = l2Nodes[j] ;
                var l2Node = new CatNode( l2NodeName, l1Node ) ;
                l1Node.addChild( l2Node ) ;
                
                l2Node.linearIndex = $scope.catLinearTree.length + 1 ;
                $scope.catLinearTree.push( l2Node ) ;
            }
        }
    }
    
    function printCatNode( catNode, indent ) {
        
        console.log( indent + '[' + catNode.depth + '] ' + catNode.displayName ) ;
        for( var i=0; i<catNode.childNodes.length; i++ ) {
            var node = catNode.childNodes[i] ;
            printCatNode( node, indent + "    " ) ;
        }
    }
    
    function printLinearTree() {
        
        for( var i=0; i<$scope.catLinearTree.length; i++ ) {
            var node = $scope.catLinearTree[i] ;
            var indent = "" ;
            for( var j=0; j<node.depth; j++ ) {
                indent += "   " ;
            }
            console.log( indent + node.linearIndex + "  " + node.displayName ) ;
        }
    }
} ) ;