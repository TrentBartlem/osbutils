(:: pragma bea:global-element-parameter parameter="$booklist1" element="ns0:BookList" location="Book.xsd" ::)
(:: pragma bea:global-element-parameter parameter="$booklist2" element="ns0:BookList" location="Book.xsd" ::)
(:: pragma bea:global-element-return element="ns0:BookList" location="Book.xsd" ::)

declare namespace ns0 = "http://www.example.org/bookstore";
declare namespace xf = "http://tempuri.org/ExampleOSBProject/Test/Merge/";

declare function xf:Merge($booklist1 as element(ns0:BookList),
    $booklist2 as element(ns0:BookList))
    as element(ns0:BookList) {
        <ns0:BookList>
            <ns0:Book>
                {
                    for $book  in ($booklist1/ns0:Book, $booklist2/ns0:Book)  
                    let $let-var1  := ()  
                    return
                        ($book)
                }
</ns0:Book>
        </ns0:BookList>
};

declare variable $booklist1 as element(ns0:BookList) external;
declare variable $booklist2 as element(ns0:BookList) external;

xf:Merge($booklist1,
    $booklist2)
