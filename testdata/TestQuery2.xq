(:: pragma bea:global-element-parameter parameter="$test" element="ns0:Test" location="TestSchema.xsd" ::)
(:: pragma bea:global-element-return element="ns1:Test2" location="TestSchema2.xsd" ::)

declare namespace ns1 = "http://www.example.org/test2";
declare namespace ns0 = "http://www.example.org/test";
declare namespace xf = "http://tempuri.org/SamplesOSBPProject/Test/TestQuery2/";

declare function xf:TestQuery2($test as element(ns0:Test))
    as element(ns1:Test2) {
        <ns1:Test2 attr = "{ data($test/@attrA) }">
            <ns1:elem>{ concat($test/ns0:elemA , $test/ns0:elemB) }</ns1:elem>
        </ns1:Test2>
};

declare variable $test as element(ns0:Test) external;

xf:TestQuery2($test)
