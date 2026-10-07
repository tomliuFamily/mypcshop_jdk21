export function addRecentProduct(
    product
) {

    if (!product || !product.id) {
        return;
    }


    try {

        const saved =
            localStorage.getItem(
                "recentProducts"
            );


        let products = [];


        if (saved) {

            const parsed =
                JSON.parse(saved);

            if (Array.isArray(parsed)) {
                products = parsed;
            }
        }


        // 移除同一商品舊紀錄
        products =
            products.filter(
                item =>
                    item.id !== product.id
            );


        // 最新的放第一筆
        products.unshift({
            id: product.id,
            name: product.name,
            price: product.price,
            category: product.category,
            image: product.image,
            stock: product.stock
        });


        // 最多保留 10 筆
        products =
            products.slice(
                0,
                10
            );


        localStorage.setItem(
            "recentProducts",
            JSON.stringify(products)
        );


    } catch (error) {

        console.error(
            "最近瀏覽商品紀錄失敗：",
            error
        );
    }
}