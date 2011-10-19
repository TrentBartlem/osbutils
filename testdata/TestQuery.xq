(:: pragma bea:global-element-parameter parameter="$test" element="ns0:Test" location="TestSchema.xsd" ::)

declare namespace ns0 = "http://www.example.org/test";
declare namespace xf = "http://tempuri.org/SamplesOSBPProject/Test/TestQuery/";

declare function xf:TestQuery($test as element(ns0:Test))
    as xs:string {
        concat($test/ns0:elemA , " ", $test/ns0:elemB)
};

declare variable $test as element(ns0:Test) external;

xf:TestQuery($test)
