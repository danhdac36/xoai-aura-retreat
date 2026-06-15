const menuData = [
    {
        "id": 1,
        "itemName": "Phở bò",
        "price": 90000,
        "ingredient": "Vietnam - bánh phở, thịt bò, hành lá, rau thơm, quế, hồi",
        "isAvailable": true
    },
    {
        "id": 2,
        "itemName": "Phở gà",
        "price": 85000,
        "ingredient": "Vietnam - bánh phở, thịt gà, hành lá, rau thơm, gừng",
        "isAvailable": true
    },
    {
        "id": 3,
        "itemName": "Bún bò Huế",
        "price": 95000,
        "ingredient": "Vietnam - bún, thịt bò, sả, ớt, mắm ruốc",
        "isAvailable": true
    },
    {
        "id": 4,
        "itemName": "Bún chả",
        "price": 85000,
        "ingredient": "Vietnam - bún, thịt heo nướng, rau sống, nước mắm",
        "isAvailable": true
    },
    {
        "id": 5,
        "itemName": "Bún riêu cua",
        "price": 80000,
        "ingredient": "Vietnam - bún, cua đồng, cà chua, đậu phụ",
        "isAvailable": true
    },
    {
        "id": 6,
        "itemName": "Bún thịt nướng",
        "price": 85000,
        "ingredient": "Vietnam - bún, thịt heo nướng, rau sống, đậu phộng",
        "isAvailable": true
    },
    {
        "id": 7,
        "itemName": "Hủ tiếu Nam Vang",
        "price": 90000,
        "ingredient": "Vietnam - hủ tiếu, tôm, thịt heo, trứng cút",
        "isAvailable": true
    },
    {
        "id": 8,
        "itemName": "Mì Quảng",
        "price": 85000,
        "ingredient": "Vietnam - mì quảng, thịt gà, tôm, đậu phộng, rau thơm",
        "isAvailable": true
    },
    {
        "id": 9,
        "itemName": "Cao lầu",
        "price": 85000,
        "ingredient": "Vietnam - mì cao lầu, thịt heo, rau sống, bánh đa",
        "isAvailable": true
    },
    {
        "id": 10,
        "itemName": "Bánh canh cua",
        "price": 95000,
        "ingredient": "Vietnam - bánh canh, cua, tôm, hành lá",
        "isAvailable": true
    },
    {
        "id": 11,
        "itemName": "Bánh cuốn",
        "price": 70000,
        "ingredient": "Vietnam - bột gạo, thịt băm, mộc nhĩ, hành phi",
        "isAvailable": true
    },
    {
        "id": 12,
        "itemName": "Bánh xèo",
        "price": 85000,
        "ingredient": "Vietnam - bột gạo, tôm, thịt, giá đỗ, rau sống",
        "isAvailable": true
    },
    {
        "id": 13,
        "itemName": "Bánh bèo",
        "price": 65000,
        "ingredient": "Vietnam - bột gạo, tôm khô, hành phi, nước mắm",
        "isAvailable": true
    },
    {
        "id": 14,
        "itemName": "Gỏi cuốn tôm thịt",
        "price": 70000,
        "ingredient": "Vietnam - bánh tráng, tôm, thịt heo, bún, rau sống",
        "isAvailable": true
    },
    {
        "id": 15,
        "itemName": "Nem rán",
        "price": 75000,
        "ingredient": "Vietnam - thịt heo, miến, mộc nhĩ, bánh đa nem",
        "isAvailable": true
    },
    {
        "id": 16,
        "itemName": "Nem nướng",
        "price": 85000,
        "ingredient": "Vietnam - thịt heo nướng, bánh tráng, rau sống, nước chấm",
        "isAvailable": true
    },
    {
        "id": 17,
        "itemName": "Cơm tấm sườn",
        "price": 95000,
        "ingredient": "Vietnam - cơm tấm, sườn nướng, bì, chả, trứng",
        "isAvailable": true
    },
    {
        "id": 18,
        "itemName": "Cơm gà Hội An",
        "price": 90000,
        "ingredient": "Vietnam - cơm, gà xé, nghệ, rau răm, hành tây",
        "isAvailable": true
    },
    {
        "id": 19,
        "itemName": "Cơm chiên hải sản",
        "price": 95000,
        "ingredient": "Vietnam - cơm, tôm, mực, trứng, rau củ",
        "isAvailable": true
    },
    {
        "id": 20,
        "itemName": "Cá kho tộ",
        "price": 110000,
        "ingredient": "Vietnam - cá, nước mắm, tiêu, hành, ớt",
        "isAvailable": true
    },
    {
        "id": 21,
        "itemName": "Cá nướng lá chuối",
        "price": 130000,
        "ingredient": "Vietnam - cá, lá chuối, sả, nghệ, rau thơm",
        "isAvailable": true
    },
    {
        "id": 22,
        "itemName": "Canh chua cá",
        "price": 90000,
        "ingredient": "Vietnam - cá, me, cà chua, dứa, bạc hà",
        "isAvailable": true
    },
    {
        "id": 23,
        "itemName": "Chả cá Lã Vọng",
        "price": 140000,
        "ingredient": "Vietnam - cá, thì là, nghệ, bún, đậu phộng",
        "isAvailable": true
    },
    {
        "id": 24,
        "itemName": "Gà hấp lá chanh",
        "price": 120000,
        "ingredient": "Vietnam - gà, lá chanh, gừng, muối tiêu",
        "isAvailable": true
    },
    {
        "id": 25,
        "itemName": "Tôm hấp nước dừa",
        "price": 150000,
        "ingredient": "Vietnam - tôm, nước dừa, sả, gừng",
        "isAvailable": true
    },
    {
        "id": 26,
        "itemName": "Bò lá lốt",
        "price": 95000,
        "ingredient": "Vietnam - thịt bò, lá lốt, đậu phộng, rau sống",
        "isAvailable": true
    },
    {
        "id": 27,
        "itemName": "Thịt kho trứng",
        "price": 100000,
        "ingredient": "Vietnam - thịt heo, trứng, nước dừa, nước mắm",
        "isAvailable": true
    },
    {
        "id": 28,
        "itemName": "Đậu hũ sốt nấm",
        "price": 75000,
        "ingredient": "Vietnam - đậu hũ, nấm, xì dầu, hành lá",
        "isAvailable": true
    },
    {
        "id": 29,
        "itemName": "Rau luộc kho quẹt",
        "price": 75000,
        "ingredient": "Vietnam - rau củ, thịt ba chỉ, tôm khô, nước mắm",
        "isAvailable": true
    },
    {
        "id": 30,
        "itemName": "Salad thanh long",
        "price": 80000,
        "ingredient": "Vietnam - thanh long, rau xanh, hạt điều, sốt chanh",
        "isAvailable": true
    },
    {
        "id": 31,
        "itemName": "Gỏi xoài tôm khô",
        "price": 85000,
        "ingredient": "Vietnam - xoài xanh, tôm khô, rau răm, đậu phộng",
        "isAvailable": true
    },
    {
        "id": 32,
        "itemName": "Gỏi gà xé phay",
        "price": 90000,
        "ingredient": "Vietnam - gà, bắp cải, rau răm, hành tây",
        "isAvailable": true
    },
    {
        "id": 33,
        "itemName": "Cháo cá",
        "price": 70000,
        "ingredient": "Vietnam - gạo, cá, hành lá, gừng",
        "isAvailable": true
    },
    {
        "id": 34,
        "itemName": "Cháo gà",
        "price": 70000,
        "ingredient": "Vietnam - gạo, gà, hành lá, gừng",
        "isAvailable": true
    },
    {
        "id": 35,
        "itemName": "Cháo yến mạch",
        "price": 65000,
        "ingredient": "Vietnam - yến mạch, sữa, hạt chia, trái cây",
        "isAvailable": true
    },
    {
        "id": 36,
        "itemName": "Chè hạt sen",
        "price": 60000,
        "ingredient": "Vietnam - hạt sen, đường phèn, nhãn nhục",
        "isAvailable": true
    },
    {
        "id": 37,
        "itemName": "Chè đậu xanh",
        "price": 55000,
        "ingredient": "Vietnam - đậu xanh, nước cốt dừa, đường",
        "isAvailable": true
    },
    {
        "id": 38,
        "itemName": "Thạch hạt chia",
        "price": 60000,
        "ingredient": "Vietnam - hạt chia, trái cây, sữa hạt",
        "isAvailable": true
    },
    {
        "id": 39,
        "itemName": "Yaourt trái cây",
        "price": 65000,
        "ingredient": "Vietnam - sữa chua, trái cây tươi, mật ong",
        "isAvailable": true
    },
    {
        "id": 40,
        "itemName": "Trái cây nhiệt đới",
        "price": 80000,
        "ingredient": "Vietnam - xoài, dứa, thanh long, dưa hấu",
        "isAvailable": true
    },
    {
        "id": 41,
        "itemName": "Sinh tố xoài",
        "price": 65000,
        "ingredient": "Vietnam - xoài, sữa chua, mật ong",
        "isAvailable": true
    },
    {
        "id": 42,
        "itemName": "Sinh tố bơ",
        "price": 70000,
        "ingredient": "Vietnam - bơ, sữa, mật ong",
        "isAvailable": true
    },
    {
        "id": 43,
        "itemName": "Nước ép dứa",
        "price": 60000,
        "ingredient": "Vietnam - dứa tươi",
        "isAvailable": true
    },
    {
        "id": 44,
        "itemName": "Trà sen",
        "price": 50000,
        "ingredient": "Vietnam - trà xanh, hoa sen",
        "isAvailable": true
    },
    {
        "id": 45,
        "itemName": "Trà gừng",
        "price": 50000,
        "ingredient": "Vietnam - gừng, mật ong, chanh",
        "isAvailable": true
    },
    {
        "id": 46,
        "itemName": "Trà hoa cúc",
        "price": 50000,
        "ingredient": "Vietnam - hoa cúc, mật ong",
        "isAvailable": true
    },
    {
        "id": 47,
        "itemName": "Sữa đậu nành",
        "price": 45000,
        "ingredient": "Vietnam - đậu nành, đường",
        "isAvailable": true
    },
    {
        "id": 48,
        "itemName": "Bún chay",
        "price": 75000,
        "ingredient": "Vietnam - bún, đậu hũ, nấm, rau củ",
        "isAvailable": true
    },
    {
        "id": 49,
        "itemName": "Cơm gạo lứt",
        "price": 75000,
        "ingredient": "Vietnam - gạo lứt, rau củ, đậu hũ",
        "isAvailable": true
    },
    {
        "id": 50,
        "itemName": "Canh rong biển",
        "price": 70000,
        "ingredient": "Vietnam - rong biển, đậu hũ, nấm",
        "isAvailable": true
    },
    {
        "id": 51,
        "itemName": "Mushroom Risotto",
        "price": 220000,
        "ingredient": "Europe - arborio rice, mushroom, parmesan, butter",
        "isAvailable": true
    },
    {
        "id": 52,
        "itemName": "Pasta Carbonara",
        "price": 210000,
        "ingredient": "Europe - spaghetti, egg, parmesan, bacon",
        "isAvailable": true
    },
    {
        "id": 53,
        "itemName": "Pasta Pesto",
        "price": 200000,
        "ingredient": "Europe - pasta, basil, pine nuts, olive oil, parmesan",
        "isAvailable": true
    },
    {
        "id": 54,
        "itemName": "Beef Lasagna",
        "price": 240000,
        "ingredient": "Europe - pasta sheets, beef, tomato sauce, cheese",
        "isAvailable": true
    },
    {
        "id": 55,
        "itemName": "Ricotta Ravioli",
        "price": 230000,
        "ingredient": "Europe - ravioli, ricotta, spinach, butter",
        "isAvailable": true
    },
    {
        "id": 56,
        "itemName": "Potato Gnocchi",
        "price": 200000,
        "ingredient": "Europe - potato, flour, tomato sauce, cheese",
        "isAvailable": true
    },
    {
        "id": 57,
        "itemName": "Pizza Margherita",
        "price": 220000,
        "ingredient": "Europe - pizza dough, tomato, mozzarella, basil",
        "isAvailable": true
    },
    {
        "id": 58,
        "itemName": "Vegetable Pizza",
        "price": 210000,
        "ingredient": "Europe - pizza dough, tomato, vegetables, cheese",
        "isAvailable": true
    },
    {
        "id": 59,
        "itemName": "Tomato Bruschetta",
        "price": 150000,
        "ingredient": "Europe - bread, tomato, basil, olive oil",
        "isAvailable": true
    },
    {
        "id": 60,
        "itemName": "Caprese Salad",
        "price": 170000,
        "ingredient": "Europe - tomato, mozzarella, basil, olive oil",
        "isAvailable": true
    },
    {
        "id": 61,
        "itemName": "Coq au Vin",
        "price": 280000,
        "ingredient": "Europe - chicken, red wine, mushroom, onion",
        "isAvailable": true
    },
    {
        "id": 62,
        "itemName": "Ratatouille",
        "price": 180000,
        "ingredient": "Europe - eggplant, zucchini, tomato, bell pepper",
        "isAvailable": true
    },
    {
        "id": 63,
        "itemName": "French Onion Soup",
        "price": 160000,
        "ingredient": "Europe - onion, beef stock, bread, cheese",
        "isAvailable": true
    },
    {
        "id": 64,
        "itemName": "Steak au Poivre",
        "price": 350000,
        "ingredient": "Europe - beef steak, pepper, cream sauce",
        "isAvailable": true
    },
    {
        "id": 65,
        "itemName": "Croque Monsieur",
        "price": 180000,
        "ingredient": "Europe - bread, ham, cheese, bechamel",
        "isAvailable": true
    },
    {
        "id": 66,
        "itemName": "Quiche Lorraine",
        "price": 190000,
        "ingredient": "Europe - egg, cream, bacon, pastry",
        "isAvailable": true
    },
    {
        "id": 67,
        "itemName": "Duck Confit",
        "price": 320000,
        "ingredient": "Europe - duck leg, potato, herbs",
        "isAvailable": true
    },
    {
        "id": 68,
        "itemName": "Niçoise Salad",
        "price": 210000,
        "ingredient": "Europe - tuna, egg, olive, green beans",
        "isAvailable": true
    },
    {
        "id": 69,
        "itemName": "Crème Brûlée",
        "price": 120000,
        "ingredient": "Europe - cream, egg yolk, sugar, vanilla",
        "isAvailable": true
    },
    {
        "id": 70,
        "itemName": "Mille-feuille",
        "price": 130000,
        "ingredient": "Europe - puff pastry, cream, sugar",
        "isAvailable": true
    },
    {
        "id": 71,
        "itemName": "Seafood Paella",
        "price": 300000,
        "ingredient": "Europe - rice, shrimp, squid, mussel, saffron",
        "isAvailable": true
    },
    {
        "id": 72,
        "itemName": "Vegetable Paella",
        "price": 240000,
        "ingredient": "Europe - rice, vegetables, saffron, olive oil",
        "isAvailable": true
    },
    {
        "id": 73,
        "itemName": "Gazpacho",
        "price": 140000,
        "ingredient": "Europe - tomato, cucumber, pepper, olive oil",
        "isAvailable": true
    },
    {
        "id": 74,
        "itemName": "Tortilla Española",
        "price": 160000,
        "ingredient": "Europe - egg, potato, onion, olive oil",
        "isAvailable": true
    },
    {
        "id": 75,
        "itemName": "Croquetas",
        "price": 150000,
        "ingredient": "Europe - potato, cheese, ham, breadcrumbs",
        "isAvailable": true
    },
    {
        "id": 76,
        "itemName": "Gambas al Ajillo",
        "price": 260000,
        "ingredient": "Europe - shrimp, garlic, olive oil, chili",
        "isAvailable": true
    },
    {
        "id": 77,
        "itemName": "Churros",
        "price": 110000,
        "ingredient": "Europe - flour, sugar, cinnamon, chocolate",
        "isAvailable": true
    },
    {
        "id": 78,
        "itemName": "Seafood Tapas",
        "price": 280000,
        "ingredient": "Europe - shrimp, squid, mussel, olive oil",
        "isAvailable": true
    },
    {
        "id": 79,
        "itemName": "Grilled Octopus",
        "price": 320000,
        "ingredient": "Europe - octopus, potato, paprika, olive oil",
        "isAvailable": true
    },
    {
        "id": 80,
        "itemName": "Iberian Ham Plate",
        "price": 350000,
        "ingredient": "Europe - iberian ham, bread, olive",
        "isAvailable": true
    },
    {
        "id": 81,
        "itemName": "Schnitzel",
        "price": 240000,
        "ingredient": "Europe - pork or chicken, breadcrumbs, potato",
        "isAvailable": true
    },
    {
        "id": 82,
        "itemName": "Beef Goulash",
        "price": 250000,
        "ingredient": "Europe - beef, paprika, onion, potato",
        "isAvailable": true
    },
    {
        "id": 83,
        "itemName": "German Potato Salad",
        "price": 150000,
        "ingredient": "Europe - potato, mustard, onion, parsley",
        "isAvailable": true
    },
    {
        "id": 84,
        "itemName": "Pretzel",
        "price": 90000,
        "ingredient": "Europe - flour, salt, butter",
        "isAvailable": true
    },
    {
        "id": 85,
        "itemName": "Grilled Sausage",
        "price": 220000,
        "ingredient": "Europe - sausage, mustard, sauerkraut",
        "isAvailable": true
    },
    {
        "id": 86,
        "itemName": "Smoked Salmon",
        "price": 260000,
        "ingredient": "Europe - salmon, dill, lemon, rye bread",
        "isAvailable": true
    },
    {
        "id": 87,
        "itemName": "Nordic Fish Soup",
        "price": 220000,
        "ingredient": "Europe - fish, cream, potato, dill",
        "isAvailable": true
    },
    {
        "id": 88,
        "itemName": "Rye Bread Sandwich",
        "price": 160000,
        "ingredient": "Europe - rye bread, salmon, cucumber, cream cheese",
        "isAvailable": true
    },
    {
        "id": 89,
        "itemName": "Swedish Meatballs",
        "price": 230000,
        "ingredient": "Europe - beef meatballs, cream sauce, potato",
        "isAvailable": true
    },
    {
        "id": 90,
        "itemName": "Mashed Potato Bowl",
        "price": 140000,
        "ingredient": "Europe - potato, butter, cream, herbs",
        "isAvailable": true
    },
    {
        "id": 91,
        "itemName": "Avocado Toast",
        "price": 150000,
        "ingredient": "Europe - bread, avocado, egg, tomato",
        "isAvailable": true
    },
    {
        "id": 92,
        "itemName": "Greek Yogurt Bowl",
        "price": 130000,
        "ingredient": "Europe - greek yogurt, berries, granola, honey",
        "isAvailable": true
    },
    {
        "id": 93,
        "itemName": "Grilled Chicken Salad",
        "price": 190000,
        "ingredient": "Europe - chicken, lettuce, tomato, dressing",
        "isAvailable": true
    },
    {
        "id": 94,
        "itemName": "Caesar Salad",
        "price": 170000,
        "ingredient": "Europe - lettuce, parmesan, crouton, caesar dressing",
        "isAvailable": true
    },
    {
        "id": 95,
        "itemName": "Quinoa Bowl",
        "price": 180000,
        "ingredient": "Europe - quinoa, chickpeas, avocado, vegetables",
        "isAvailable": true
    },
    {
        "id": 96,
        "itemName": "Roasted Vegetables",
        "price": 160000,
        "ingredient": "Europe - carrot, zucchini, potato, olive oil",
        "isAvailable": true
    },
    {
        "id": 97,
        "itemName": "Pumpkin Soup",
        "price": 150000,
        "ingredient": "Europe - pumpkin, cream, onion, herbs",
        "isAvailable": true
    },
    {
        "id": 98,
        "itemName": "Herb Salmon",
        "price": 300000,
        "ingredient": "Europe - salmon, herbs, lemon, vegetables",
        "isAvailable": true
    },
    {
        "id": 99,
        "itemName": "Fruit Tart",
        "price": 130000,
        "ingredient": "Europe - pastry, custard, fruit",
        "isAvailable": true
    },
    {
        "id": 100,
        "itemName": "Granola Bowl",
        "price": 120000,
        "ingredient": "Europe - granola, yogurt, fruit, honey",
        "isAvailable": true
    },
    {
        "id": 101,
        "itemName": "Salmon Sushi",
        "price": 180000,
        "ingredient": "Asia - rice, salmon, seaweed, wasabi",
        "isAvailable": true
    },
    {
        "id": 102,
        "itemName": "Vegetarian Sushi",
        "price": 150000,
        "ingredient": "Asia - rice, seaweed, cucumber, avocado",
        "isAvailable": true
    },
    {
        "id": 103,
        "itemName": "Sashimi",
        "price": 280000,
        "ingredient": "Asia - fresh fish, soy sauce, wasabi",
        "isAvailable": true
    },
    {
        "id": 104,
        "itemName": "Ramen",
        "price": 190000,
        "ingredient": "Asia - noodles, broth, egg, pork",
        "isAvailable": true
    },
    {
        "id": 105,
        "itemName": "Udon",
        "price": 170000,
        "ingredient": "Asia - udon noodles, broth, scallion",
        "isAvailable": true
    },
    {
        "id": 106,
        "itemName": "Tempura",
        "price": 200000,
        "ingredient": "Asia - shrimp, vegetables, tempura batter",
        "isAvailable": true
    },
    {
        "id": 107,
        "itemName": "Donburi",
        "price": 180000,
        "ingredient": "Asia - rice, egg, meat, sauce",
        "isAvailable": true
    },
    {
        "id": 108,
        "itemName": "Yakitori",
        "price": 170000,
        "ingredient": "Asia - chicken, soy sauce, mirin",
        "isAvailable": true
    },
    {
        "id": 109,
        "itemName": "Miso Soup",
        "price": 90000,
        "ingredient": "Asia - miso, tofu, seaweed, scallion",
        "isAvailable": true
    },
    {
        "id": 110,
        "itemName": "Matcha Dessert",
        "price": 120000,
        "ingredient": "Asia - matcha, cream, sugar",
        "isAvailable": true
    },
    {
        "id": 111,
        "itemName": "Bibimbap",
        "price": 180000,
        "ingredient": "Asia - rice, beef, vegetables, egg, gochujang",
        "isAvailable": true
    },
    {
        "id": 112,
        "itemName": "Bulgogi",
        "price": 240000,
        "ingredient": "Asia - beef, soy sauce, pear, sesame",
        "isAvailable": true
    },
    {
        "id": 113,
        "itemName": "Japchae",
        "price": 170000,
        "ingredient": "Asia - glass noodles, vegetables, sesame oil",
        "isAvailable": true
    },
    {
        "id": 114,
        "itemName": "Kimchi Jjigae",
        "price": 170000,
        "ingredient": "Asia - kimchi, pork, tofu, chili",
        "isAvailable": true
    },
    {
        "id": 115,
        "itemName": "Tteokbokki",
        "price": 140000,
        "ingredient": "Asia - rice cake, chili sauce, fish cake",
        "isAvailable": true
    },
    {
        "id": 116,
        "itemName": "Samgyeopsal",
        "price": 260000,
        "ingredient": "Asia - pork belly, lettuce, garlic, sauce",
        "isAvailable": true
    },
    {
        "id": 117,
        "itemName": "Gimbap",
        "price": 130000,
        "ingredient": "Asia - rice, seaweed, vegetables, egg",
        "isAvailable": true
    },
    {
        "id": 118,
        "itemName": "Samgyetang",
        "price": 220000,
        "ingredient": "Asia - chicken, ginseng, rice, garlic",
        "isAvailable": true
    },
    {
        "id": 119,
        "itemName": "Hotteok",
        "price": 90000,
        "ingredient": "Asia - flour, brown sugar, cinnamon, nuts",
        "isAvailable": true
    },
    {
        "id": 120,
        "itemName": "Bingsu",
        "price": 130000,
        "ingredient": "Asia - shaved ice, milk, fruit, red bean",
        "isAvailable": true
    },
    {
        "id": 121,
        "itemName": "Pad Thai",
        "price": 170000,
        "ingredient": "Asia - rice noodles, shrimp, egg, tamarind",
        "isAvailable": true
    },
    {
        "id": 122,
        "itemName": "Tom Yum",
        "price": 180000,
        "ingredient": "Asia - shrimp, lemongrass, lime, chili",
        "isAvailable": true
    },
    {
        "id": 123,
        "itemName": "Green Curry",
        "price": 190000,
        "ingredient": "Asia - coconut milk, chicken, green curry paste",
        "isAvailable": true
    },
    {
        "id": 124,
        "itemName": "Mango Sticky Rice",
        "price": 120000,
        "ingredient": "Asia - sticky rice, mango, coconut milk",
        "isAvailable": true
    },
    {
        "id": 125,
        "itemName": "Papaya Salad",
        "price": 120000,
        "ingredient": "Asia - green papaya, chili, lime, peanut",
        "isAvailable": true
    },
    {
        "id": 126,
        "itemName": "Massaman Curry",
        "price": 200000,
        "ingredient": "Asia - beef, potato, coconut milk, curry paste",
        "isAvailable": true
    },
    {
        "id": 127,
        "itemName": "Pineapple Fried Rice",
        "price": 160000,
        "ingredient": "Asia - rice, pineapple, shrimp, cashew",
        "isAvailable": true
    },
    {
        "id": 128,
        "itemName": "Thai Coconut Soup",
        "price": 160000,
        "ingredient": "Asia - coconut milk, chicken, galangal",
        "isAvailable": true
    },
    {
        "id": 129,
        "itemName": "Grilled River Prawn",
        "price": 300000,
        "ingredient": "Asia - river prawn, garlic, lime sauce",
        "isAvailable": true
    },
    {
        "id": 130,
        "itemName": "Thai Milk Tea",
        "price": 70000,
        "ingredient": "Asia - black tea, milk, sugar",
        "isAvailable": true
    },
    {
        "id": 131,
        "itemName": "Dim Sum",
        "price": 180000,
        "ingredient": "Asia - dumpling wrapper, shrimp, pork",
        "isAvailable": true
    },
    {
        "id": 132,
        "itemName": "Peking Duck",
        "price": 350000,
        "ingredient": "Asia - duck, pancake, cucumber, hoisin",
        "isAvailable": true
    },
    {
        "id": 133,
        "itemName": "Yangzhou Fried Rice",
        "price": 160000,
        "ingredient": "Asia - rice, egg, shrimp, vegetables",
        "isAvailable": true
    },
    {
        "id": 134,
        "itemName": "Mapo Tofu",
        "price": 150000,
        "ingredient": "Asia - tofu, chili bean paste, pork",
        "isAvailable": true
    },
    {
        "id": 135,
        "itemName": "Hand-pulled Noodles",
        "price": 170000,
        "ingredient": "Asia - wheat noodles, beef broth, scallion",
        "isAvailable": true
    },
    {
        "id": 136,
        "itemName": "Seafood Congee",
        "price": 140000,
        "ingredient": "Asia - rice, shrimp, fish, ginger",
        "isAvailable": true
    },
    {
        "id": 137,
        "itemName": "Steamed Bao",
        "price": 120000,
        "ingredient": "Asia - flour bun, pork or mushroom filling",
        "isAvailable": true
    },
    {
        "id": 138,
        "itemName": "Chinese Chrysanthemum Tea",
        "price": 60000,
        "ingredient": "Asia - chrysanthemum flower, hot water",
        "isAvailable": true
    },
    {
        "id": 139,
        "itemName": "Snow Fungus Lotus Seed Soup",
        "price": 100000,
        "ingredient": "Asia - snow fungus, lotus seed, rock sugar",
        "isAvailable": true
    },
    {
        "id": 140,
        "itemName": "Mushroom Hotpot",
        "price": 220000,
        "ingredient": "Asia - mushroom, vegetables, broth, tofu",
        "isAvailable": true
    },
    {
        "id": 141,
        "itemName": "Korean Seaweed Rice Roll",
        "price": 130000,
        "ingredient": "Asia - rice, seaweed, vegetables, sesame",
        "isAvailable": true
    },
    {
        "id": 142,
        "itemName": "Japanese Curry Rice",
        "price": 170000,
        "ingredient": "Asia - rice, curry, carrot, potato",
        "isAvailable": true
    },
    {
        "id": 143,
        "itemName": "Chicken Teriyaki",
        "price": 190000,
        "ingredient": "Asia - chicken, soy sauce, mirin, sugar",
        "isAvailable": true
    },
    {
        "id": 144,
        "itemName": "Thai Curry Rice",
        "price": 180000,
        "ingredient": "Asia - rice, curry, coconut milk, chicken",
        "isAvailable": true
    },
    {
        "id": 145,
        "itemName": "Thai Spicy Noodle Soup",
        "price": 160000,
        "ingredient": "Asia - noodles, chili, herbs, meat",
        "isAvailable": true
    },
    {
        "id": 146,
        "itemName": "Taiwanese Milk Tea",
        "price": 80000,
        "ingredient": "Asia - tea, milk, tapioca pearls",
        "isAvailable": true
    },
    {
        "id": 147,
        "itemName": "Korean Spicy Noodles",
        "price": 150000,
        "ingredient": "Asia - noodles, chili sauce, sesame",
        "isAvailable": true
    },
    {
        "id": 148,
        "itemName": "Bento Box",
        "price": 200000,
        "ingredient": "Asia - rice, fish, vegetables, egg",
        "isAvailable": true
    },
    {
        "id": 149,
        "itemName": "Japanese Grilled Salmon",
        "price": 260000,
        "ingredient": "Asia - salmon, soy sauce, mirin, rice",
        "isAvailable": true
    },
    {
        "id": 150,
        "itemName": "Asian Seafood Hotpot",
        "price": 300000,
        "ingredient": "Asia - seafood, mushroom, vegetables, broth",
        "isAvailable": true
    }
];
