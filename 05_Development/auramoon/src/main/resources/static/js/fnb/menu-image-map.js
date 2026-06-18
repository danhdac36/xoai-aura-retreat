const menuImageMap = {
    "Cá Hồi Nướng Hương Thảo": "/images/fnb/menu/ca-hoi-nuong-huong-thao.jpg",
    "Salad Aura Thanh Lọc": "/images/fnb/menu/salad-aura-thanh-loc.jpg",
    "Bát Cơm Gạo Lứt Chay": "/images/fnb/menu/bat-com-gao-lut-chay.jpg",
    "Nước Ép Cần Tây Hữu Cơ": "/images/fnb/menu/nuoc-ep-can-tay-huu-co.jpg",
    "Phở bò": "/images/fnb/menu/pho-bo.jpg",
    "Phở gà": "/images/fnb/menu/pho-ga.jpg",
    "Bún bò Huế": "/images/fnb/menu/bun-bo-hue.jpg",
    "Bún chả": "/images/fnb/menu/bun-cha.jpg",
    "Bún riêu cua": "/images/fnb/menu/bun-rieu-cua.jpg",
    "Bún thịt nướng": "/images/fnb/menu/bun-thit-nuong.jpg",
    "Hủ tiếu Nam Vang": "/images/fnb/menu/hu-tieu-nam-vang.jpg",
    "Mì Quảng": "/images/fnb/menu/mi-quang.jpg",
    "Cao lầu": "/images/fnb/menu/cao-lau.jpg",
    "Bánh canh cua": "/images/fnb/menu/banh-canh-cua.jpg",
    "Bánh cuốn": "/images/fnb/menu/banh-cuon.jpg",
    "Bánh xèo": "/images/fnb/menu/banh-xeo.jpg",
    "Bánh bèo": "/images/fnb/menu/banh-beo.jpg",
    "Gỏi cuốn tôm thịt": "/images/fnb/menu/goi-cuon-tom-thit.jpg",
    "Nem rán": "/images/fnb/menu/nem-ran.jpg",
    "Nem nướng": "/images/fnb/menu/nem-nuong.jpg",
    "Cơm tấm sườn": "/images/fnb/menu/com-tam-suon.jpg",
    "Cơm gà Hội An": "/images/fnb/menu/com-ga-hoi-an.jpg",
    "Cơm chiên hải sản": "/images/fnb/menu/com-chien-hai-san.jpg",
    "Cá kho tộ": "/images/fnb/menu/ca-kho-to.jpg",
    "Cá nướng lá chuối": "/images/fnb/menu/ca-nuong-la-chuoi.jpg",
    "Canh chua cá": "/images/fnb/menu/canh-chua-ca.jpg",
    "Chả cá Lã Vọng": "/images/fnb/menu/cha-ca-la-vong.jpg",
    "Gà hấp lá chanh": "/images/fnb/menu/ga-hap-la-chanh.jpg",
    "Tôm hấp nước dừa": "/images/fnb/menu/tom-hap-nuoc-dua.jpg",
    "Bò lá lốt": "/images/fnb/menu/bo-la-lot.jpg",
    "Thịt kho trứng": "/images/fnb/menu/thit-kho-trung.jpg",
    "Đậu hũ sốt nấm": "/images/fnb/menu/dau-hu-sot-nam.jpg",
    "Rau luộc kho quẹt": "/images/fnb/menu/rau-luoc-kho-quet.jpg",
    "Salad thanh long": "/images/fnb/menu/salad-thanh-long.jpg",
    "Gỏi xoài tôm khô": "/images/fnb/menu/goi-xoai-tom-kho.jpg",
    "Gỏi gà xé phay": "/images/fnb/menu/goi-ga-xe-phay.jpg",
    "Cháo cá": "/images/fnb/menu/chao-ca.jpg",
    "Cháo gà": "/images/fnb/menu/chao-ga.jpg",
    "Cháo yến mạch": "/images/fnb/menu/chao-yen-mach.jpg",
    "Chè hạt sen": "/images/fnb/menu/che-hat-sen.jpg",
    "Chè đậu xanh": "/images/fnb/menu/che-dau-xanh.jpg",
    "Thạch hạt chia": "/images/fnb/menu/thach-hat-chia.jpg",
    "Yaourt trái cây": "/images/fnb/menu/yaourt-trai-cay.jpg",
    "Trái cây nhiệt đới": "/images/fnb/menu/trai-cay-nhiet-doi.jpg",
    "Sinh tố xoài": "/images/fnb/menu/sinh-to-xoai.jpg",
    "Sinh tố bơ": "/images/fnb/menu/sinh-to-bo.jpg",
    "Nước ép dứa": "/images/fnb/menu/nuoc-ep-dua.jpg",
    "Trà sen": "/images/fnb/menu/tra-sen.jpg",
    "Trà gừng": "/images/fnb/menu/tra-gung.jpg",
    "Trà hoa cúc": "/images/fnb/menu/tra-hoa-cuc.jpg",
    "Sữa đậu nành": "/images/fnb/menu/sua-dau-nanh.jpg",
    "Bún chay": "/images/fnb/menu/bun-chay.jpg",
    "Cơm gạo lứt": "/images/fnb/menu/com-gao-lut.jpg",
    "Canh rong biển": "/images/fnb/menu/canh-rong-bien.jpg",

    "Mushroom Risotto": "/images/fnb/menu/mushroom-risotto.jpg",
    "Pasta Carbonara": "/images/fnb/menu/pasta-carbonara.jpg",
    "Pasta Pesto": "/images/fnb/menu/pasta-pesto.jpg",
    "Beef Lasagna": "/images/fnb/menu/beef-lasagna.jpg",
    "Ricotta Ravioli": "/images/fnb/menu/ricotta-ravioli.jpg",
    "Potato Gnocchi": "/images/fnb/menu/potato-gnocchi.jpg",
    "Pizza Margherita": "/images/fnb/menu/pizza-margherita.jpg",
    "Vegetable Pizza": "/images/fnb/menu/vegetable-pizza.jpg",
    "Tomato Bruschetta": "/images/fnb/menu/tomato-bruschetta.jpg",
    "Caprese Salad": "/images/fnb/menu/caprese-salad.jpg",
    "Coq au Vin": "/images/fnb/menu/coq-au-vin.jpg",
    "Ratatouille": "/images/fnb/menu/ratatouille.jpg",
    "French Onion Soup": "/images/fnb/menu/french-onion-soup.jpg",
    "Steak au Poivre": "/images/fnb/menu/steak-au-poivre.jpg",
    "Croque Monsieur": "/images/fnb/menu/croque-monsieur.jpg",
    "Quiche Lorraine": "/images/fnb/menu/quiche-lorraine.jpg",
    "Duck Confit": "/images/fnb/menu/duck-confit.jpg",
    "Niçoise Salad": "/images/fnb/menu/nicoise-salad.jpg",
    "Crème Brûlée": "/images/fnb/menu/creme-brulee.jpg",
    "Mille-feuille": "/images/fnb/menu/mille-feuille.jpg",
    "Seafood Paella": "/images/fnb/menu/seafood-paella.jpg",
    "Vegetable Paella": "/images/fnb/menu/vegetable-paella.jpg",
    "Gazpacho": "/images/fnb/menu/gazpacho.jpg",
    "Tortilla Española": "/images/fnb/menu/tortilla-espanola.jpg",
    "Croquetas": "/images/fnb/menu/croquetas.jpg",
    "Gambas al Ajillo": "/images/fnb/menu/gambas-al-ajillo.jpg",
    "Churros": "/images/fnb/menu/churros.jpg",
    "Seafood Tapas": "/images/fnb/menu/seafood-tapas.jpg",
    "Grilled Octopus": "/images/fnb/menu/grilled-octopus.jpg",
    "Iberian Ham Plate": "/images/fnb/menu/iberian-ham-plate.jpg",
    "Schnitzel": "/images/fnb/menu/schnitzel.jpg",
    "Beef Goulash": "/images/fnb/menu/beef-goulash.jpg",
    "German Potato Salad": "/images/fnb/menu/german-potato-salad.jpg",
    "Pretzel": "/images/fnb/menu/pretzel.jpg",
    "Grilled Sausage": "/images/fnb/menu/grilled-sausage.jpg",
    "Smoked Salmon": "/images/fnb/menu/smoked-salmon.jpg",
    "Nordic Fish Soup": "/images/fnb/menu/nordic-fish-soup.jpg",
    "Rye Bread Sandwich": "/images/fnb/menu/rye-bread-sandwich.jpg",
    "Swedish Meatballs": "/images/fnb/menu/swedish-meatballs.jpg",
    "Mashed Potato Bowl": "/images/fnb/menu/mashed-potato-bowl.jpg",
    "Avocado Toast": "/images/fnb/menu/avocado-toast.jpg",
    "Greek Yogurt Bowl": "/images/fnb/menu/greek-yogurt-bowl.jpg",
    "Grilled Chicken Salad": "/images/fnb/menu/grilled-chicken-salad.jpg",
    "Caesar Salad": "/images/fnb/menu/caesar-salad.jpg",
    "Quinoa Bowl": "/images/fnb/menu/quinoa-bowl.jpg",
    "Roasted Vegetables": "/images/fnb/menu/roasted-vegetables.jpg",
    "Pumpkin Soup": "/images/fnb/menu/pumpkin-soup.jpg",
    "Herb Salmon": "/images/fnb/menu/herb-salmon.jpg",
    "Fruit Tart": "/images/fnb/menu/fruit-tart.jpg",
    "Granola Bowl": "/images/fnb/menu/granola-bowl.jpg",

    "Salmon Sushi": "/images/fnb/menu/salmon-sushi.jpg",
    "Vegetarian Sushi": "/images/fnb/menu/vegetarian-sushi.jpg",
    "Sashimi": "/images/fnb/menu/sashimi.jpg",
    "Ramen": "/images/fnb/menu/ramen.jpg",
    "Udon": "/images/fnb/menu/udon.jpg",
    "Tempura": "/images/fnb/menu/tempura.jpg",
    "Donburi": "/images/fnb/menu/donburi.jpg",
    "Yakitori": "/images/fnb/menu/yakitori.jpg",
    "Miso Soup": "/images/fnb/menu/miso-soup.jpg",
    "Matcha Dessert": "/images/fnb/menu/matcha-dessert.jpg",
    "Bibimbap": "/images/fnb/menu/bibimbap.jpg",
    "Bulgogi": "/images/fnb/menu/bulgogi.jpg",
    "Japchae": "/images/fnb/menu/japchae.jpg",
    "Kimchi Jjigae": "/images/fnb/menu/kimchi-jjigae.jpg",
    "Tteokbokki": "/images/fnb/menu/tteokbokki.jpg",
    "Samgyeopsal": "/images/fnb/menu/samgyeopsal.jpg",
    "Gimbap": "/images/fnb/menu/gimbap.jpg",
    "Samgyetang": "/images/fnb/menu/samgyetang.jpg",
    "Hotteok": "/images/fnb/menu/hotteok.jpg",
    "Bingsu": "/images/fnb/menu/bingsu.jpg",
    "Pad Thai": "/images/fnb/menu/pad-thai.jpg",
    "Tom Yum": "/images/fnb/menu/tom-yum.jpg",
    "Green Curry": "/images/fnb/menu/green-curry.jpg",
    "Mango Sticky Rice": "/images/fnb/menu/mango-sticky-rice.jpg",
    "Papaya Salad": "/images/fnb/menu/papaya-salad.jpg",
    "Massaman Curry": "/images/fnb/menu/massaman-curry.jpg",
    "Pineapple Fried Rice": "/images/fnb/menu/pineapple-fried-rice.jpg",
    "Thai Coconut Soup": "/images/fnb/menu/thai-coconut-soup.jpg",
    "Grilled River Prawn": "/images/fnb/menu/grilled-river-prawn.jpg",
    "Thai Milk Tea": "/images/fnb/menu/thai-milk-tea.jpg",
    "Dim Sum": "/images/fnb/menu/dim-sum.jpg",
    "Peking Duck": "/images/fnb/menu/peking-duck.jpg",
    "Yangzhou Fried Rice": "/images/fnb/menu/yangzhou-fried-rice.jpg",
    "Mapo Tofu": "/images/fnb/menu/mapo-tofu.jpg",
    "Hand-pulled Noodles": "/images/fnb/menu/hand-pulled-noodles.jpg",
    "Seafood Congee": "/images/fnb/menu/seafood-congee.jpg",
    "Steamed Bao": "/images/fnb/menu/steamed-bao.jpg",
    "Chinese Chrysanthemum Tea": "/images/fnb/menu/chinese-chrysanthemum-tea.jpg",
    "Snow Fungus Lotus Seed Soup": "/images/fnb/menu/snow-fungus-lotus-seed-soup.jpg",
    "Mushroom Hotpot": "/images/fnb/menu/mushroom-hotpot.jpg",
    "Korean Seaweed Rice Roll": "/images/fnb/menu/korean-seaweed-rice-roll.jpg",
    "Japanese Curry Rice": "/images/fnb/menu/japanese-curry-rice.jpg",
    "Chicken Teriyaki": "/images/fnb/menu/chicken-teriyaki.jpg",
    "Thai Curry Rice": "/images/fnb/menu/thai-curry-rice.jpg",
    "Thai Spicy Noodle Soup": "/images/fnb/menu/thai-spicy-noodle-soup.jpg",
    "Taiwanese Milk Tea": "/images/fnb/menu/taiwanese-milk-tea.jpg",
    "Korean Spicy Noodles": "/images/fnb/menu/korean-spicy-noodles.jpg",
    "Bento Box": "/images/fnb/menu/bento-box.jpg",
    "Japanese Grilled Salmon": "/images/fnb/menu/japanese-grilled-salmon.jpg",
    "Asian Seafood Hotpot": "/images/fnb/menu/asian-seafood-hotpot.jpg"
};

function removeVietnameseTones(str) {
    str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a");
    str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e");
    str = str.replace(/ì|í|ị|ỉ|ĩ/g, "i");
    str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o");
    str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u");
    str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y");
    str = str.replace(/đ/g, "d");
    str = str.replace(/À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ/g, "a");
    str = str.replace(/È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ/g, "e");
    str = str.replace(/Ì|Í|Ị|Ỉ|Ĩ/g, "i");
    str = str.replace(/Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ/g, "o");
    str = str.replace(/Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ/g, "u");
    str = str.replace(/Ỳ|Ý|Ỵ|Ỷ|Ỹ/g, "y");
    str = str.replace(/Đ/g, "d");
    str = str.replace(/\u0300|\u0301|\u0303|\u0309|\u0323/g, "");
    str = str.replace(/\u02C6|\u0306|\u031B/g, "");
    return str.trim();
}

function getMenuImageUrl(item) {
    if (!item || !item.itemName) {
        return "/images/fnb/menu/default-food.jpg";
    }

    if (menuImageMap[item.itemName]) {
        return menuImageMap[item.itemName];
    }

    const cleanName = removeVietnameseTones(item.itemName)
        .toLowerCase()
        .replace(/[^a-z0-9\s-]/g, "")
        .replace(/\s+/g, "-")
        .replace(/-+/g, "-");

    return `/images/fnb/menu/${cleanName}.jpg`;
}
