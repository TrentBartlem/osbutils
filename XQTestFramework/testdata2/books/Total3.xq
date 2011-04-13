(:: pragma bea:global-element-parameter parameter="$booklist" element="ns0:BookList" location="Book.xsd" ::)

declare namespace ns0 = "http://www.example.org/bookstore";
declare namespace xf = "http://tempuri.org/ExampleOSBProject/Test/Total/";

declare function xf:Total($booklist as element(ns0:BookList))
    as xs:double {
        fn:sum($booklist/ns0:Book/ns0:Price)
};

declare variable $booklist as element(ns0:BookList) external;

xf:Total($booklist)
