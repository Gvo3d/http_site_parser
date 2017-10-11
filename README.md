ABOUTYOU PARSER
by Yakimov Denis
denis.yakimov89@gmail.com
11.10.2017

Target site: https://www.aboutyou.de

Manual test:
You can test algoritm manually, by accessing link: https://www.aboutyou.de/suche?term=Premium&category=138113
for now it has a 18 products.
Valid command for starting algoritm:
java -jar parser.jar  Premium&category=138113

Launch:
You can launch parser in two algoritm variants - keyword search and full site map.
Second one will make the full site map, choose all products from that data and will save that info in file, but full site map may cost to you some memory, so I suggest you to launch parser that way with key: -Xmx4096M.
To launch parser in second modem just don't enter any keyword, or enter it like "".

Program options:
No. Type    Description
0 (String)  search keyword
1 (int)     connection timeout
2 (int)     request timeout
3 (int)     socket timeout
4 (boolean) attach pretty headers

Software fetching data from site with multiple threads, writing memory usage, products and pages counters and elapsed time statistics. It uses one http request to get data for parsing and most of the data parsed from HTML code, only color parsed from JSON with page load data.
MultiThreading class - HttpDataFetcher, it simultaneously reads, parses data and collecting it in concurrent hash set. The count of threads that are working at a single time is up to your physical and virtual cores count;


P.S. If you have problems with socket timeout connection or else - add the custom timeouts like:
${searcheable} 8000 8000 8000 true
or for full algo:
"" 10000 10000 10000 true 