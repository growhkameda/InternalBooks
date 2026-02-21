[1mdiff --git a/InternalBooks/src/main/java/com/example/internalbooks/controller/InternalBooksController.java b/InternalBooks/src/main/java/com/example/internalbooks/controller/InternalBooksController.java[m
[1mindex cd0a700..092a75b 100644[m
[1m--- a/InternalBooks/src/main/java/com/example/internalbooks/controller/InternalBooksController.java[m
[1m+++ b/InternalBooks/src/main/java/com/example/internalbooks/controller/InternalBooksController.java[m
[36m@@ -216,10 +216,12 @@[m [mpublic class InternalBooksController {[m
             screenFlag = 1;[m
 [m
             // カテゴリーに属するすべての本のIDを取得[m
[31m-            List<Integer> allBookIds = tBookService.getCategoriesdetail(category);[m
[32m+[m[32m//            List<Integer> allBookIds = tBookService.getCategoriesdetail(category);[m
[32m+[m
[32m+[m[32m            List<DtoBookInfo> allBooks = tBookService.getBooksByCategoryWithDetails(category);[m
 [m
             // 取得した本の要素数を取得[m
[31m-            int TOTAL_BOOK_COUNT = allBookIds.size();[m
[32m+[m[32m            int TOTAL_BOOK_COUNT = allBooks.size();[m
 [m
             // 指定した表示画像数と、取得した要素数で必要なページ数を計算[m
             int totalPages = (int) Math.ceil((double) TOTAL_BOOK_COUNT / BOOKS_PER_PAGE);[m
[36m@@ -229,10 +231,12 @@[m [mpublic class InternalBooksController {[m
             int toIndex = Math.min(fromIndex + BOOKS_PER_PAGE, TOTAL_BOOK_COUNT);[m
 [m
             // 表示対象の本IDリストを抽出[m
[31m-            List<Integer> pagedBookIds = allBookIds.subList(fromIndex, toIndex);[m
[32m+[m[32m//            List<Integer> pagedBookIds = allBookIds.subList(fromIndex, toIndex);[m
[32m+[m[32m            List<DtoBookInfo> pagedBookIds = allBooks.subList(fromIndex, toIndex);[m
 [m
             // Viewに渡すモデル属性を設定[m
[31m-            model.addAttribute("bookIdList", pagedBookIds);[m
[32m+[m[32m//            model.addAttribute("bookIdList", pagedBookIds);[m
[32m+[m[32m            model.addAttribute("bookList", pagedBookIds);[m
             model.addAttribute("category", category);[m
             model.addAttribute("currentPage", page);[m
             model.addAttribute("totalPages", totalPages);[m
[1mdiff --git a/InternalBooks/src/main/resources/templates/page/categories_detail.html b/InternalBooks/src/main/resources/templates/page/categories_detail.html[m
[1mindex 696f8cf..1d8d15e 100644[m
[1m--- a/InternalBooks/src/main/resources/templates/page/categories_detail.html[m
[1m+++ b/InternalBooks/src/main/resources/templates/page/categories_detail.html[m
[36m@@ -45,51 +45,43 @@[m
 			</div>[m
 		</div>[m
 [m
[31m-		<div class="container fixed-page-width">[m
[31m-			<div id="image-gallery" class="row" style="justify-content: flex-start; margin-bottom: 40px;">[m
[31m-				<!--コントローラーからbookIdListを受け取り、bookIdに格納[m
[31m-				「page」内の「searchresult.html」に飛ぶボタン[m
[31m-				「searchresult.html」飛ぶ際に、クリックした本のbookIdを渡す-->[m
[31m-				<a th:each="bookId : ${bookIdList}" th:href="@{'/page/searchresult?bookId=' + ${bookId}}"[m
[31m-					class="col-md-4 mb-4 btn" style="text-decoration: none;">[m
[31m-					<img th:src="@{'/images/' + ${bookId} + '.png'}" alt="Book Image"[m
[31m-						class="img-fluid fixed-size-img" />[m
[31m-				</a>[m
[31m-			</div>[m
[31m-[m
[31m-			<div th:if="${#lists.isEmpty(bookIdList)}">[m
[31m-				<p>該当する本が見つかりませんでした。</p>[m
[31m-			</div>[m
[31m-[m
[31m-			<div class="pagination-buttons fixed-page-width">[m
[31m-[m
[31m-				<!--左側に「← 前のページ」ボタンを表示-->[m
[31m-				<!--左側に表示するように指定-->[m
[31m-				<div class="text-start">[m
[31m-					<div th:if="${currentPage > 0}">[m
[31m-						<a th:href="@{/page/categories_detail(category=${category}, page=${currentPage - 1})}"[m
[31m-							class="btn btn-outline-primary">[m
[31m-							← 前のページ[m
[31m-						</a>[m
[31m-					</div>[m
[31m-				</div>[m
[31m-[m
[31m-				<!--右側に「次のページ →」ボタンを表示-->[m
[31m-				<div th:if="${currentPage + 1 < totalPages}">[m
[31m-					<!--右側に表示するように指定-->[m
[31m-					<div class="text-end">[m
[31m-						<a th:href="@{/page/categories_detail(category=${category}, page=${currentPage + 1})}"[m
[31m-							class="btn btn-outline-primary">[m
[31m-							次のページ →[m
[31m-						</a>[m
[31m-					</div>[m
[31m-				</div>[m
[31m-[m
[32m+[m[32m    <div class="container fixed-page-width">[m
[32m+[m		[32m<div id="image-gallery" class="row" style="justify-content: flex-start; margin-bottom: 40px;">[m
[32m+[m		[41m [m	[32m<!-- bookList をループ -->[m
[32m+[m			[32m<div th:each="book : ${bookList}" class="col-md-4 mb-4">[m
[32m+[m		[32m    <!-- 貸出可能：クリック可能 -->[m
[32m+[m		[32m    <a th:if="${book.status == '貸出可能'}"[m
[32m+[m		[32m       th:href="@{'/page/searchresult?bookid=' + ${book.bookId}}"[m
[32m+[m		[32m       class="btn d-block p-0"[m
[32m+[m		[32m       style="text-decoration: none;">[m
[32m+[m		[32m      <img th:src="@{'/images/' + ${book.bookId} + '.png'}"[m
[32m+[m		[32m           alt="Book Image"[m
[32m+[m		[32m           class="img-fluid fixed-size-img" />[m
[32m+[m		[32m    </a>[m
[32m+[m		[32m    <!-- 貸出中：クリック不可 + グレーアウト -->[m
[32m+[m		[32m    <div th:if="${book.status == '貸出中'}"[m
[32m+[m		[32m         class="d-inline-block"[m
[32m+[m		[32m         style="position: relative; cursor: not-allowed;">[m
[32m+[m		[32m      <img th:src="@{'/images/' + ${book.bookId} + '.png'}"[m
[32m+[m		[32m           alt="Book Image (貸出中)"[m
[32m+[m		[32m           class="img-fluid fixed-size-img"[m
[32m+[m		[32m           style="opacity: 0.5; filter: grayscale(100%) brightness(0.6);" />[m
[32m+[m		[32m      <!-- 貸出中ラベル -->[m
[32m+[m		[32m      <div style="position: absolute; top: 50%; left: 50%;[m
[32m+[m		[32m                  transform: translate(-50%, -50%);[m
[32m+[m		[32m                  background: rgba(0, 0, 0, 0.75);[m
[32m+[m		[32m                  color: white; padding: 10px 20px;[m
[32m+[m		[32m                  border-radius: 5px; font-weight: bold;[m
[32m+[m		[32m                  font-size: 16px; white-space: nowrap;">[m
[32m+[m		[32m        貸出中[m
[32m+[m		[32m      </div>[m
[32m+[m		[41m [m	[32m</div>[m
 			</div>[m
[32m+[m		[32m</div>[m
[32m+[m	[32m</div>[m
 [m
[32m+[m[32m  </section>[m
 [m
[31m-		</div>[m
[31m-	</section>[m
 </body>[m
 [m
 </html>[m
\ No newline at end of file[m
