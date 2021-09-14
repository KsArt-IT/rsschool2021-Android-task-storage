package ru.ksart.potatohandbook.model.db

import ru.ksart.potatohandbook.model.data.PeriodRipening
import ru.ksart.potatohandbook.model.data.PotatoVariety
import ru.ksart.potatohandbook.model.data.Productivity

// первоначальное заполнение базы данных
class PotatoDatabaseInitial {

    val potatoInitData: List<Potato> = listOf(
        Potato(
            id = 1,
            name = "Адретта",
            description = "Картофель имеет великолепные вкусовые качества, хотя изначально сорт создавался как кормовая разновидность, поскольку имеет желтый цвет кожуры.Сорт «Адретта» был выведен германскими селекционерами не так давно (около 25 лет назад), но сразу был по достоинству оценен гурманами, поэтому быстро приобрел популярность и стал широко использоваться в кулинарии благодаря своим высоким вкусовым качествам.",
            imageUri = null,
            imageUrl = "https://agrostory.com/upload/medialibrary/318/318ebe76512cd361a73711997b7ce801.jpg",
            variety = PotatoVariety.Table,
            ripening = PeriodRipening.Early,
            productivity = Productivity.High,
        ),
        Potato(
            id = 2,
            name = "Зарево",
            description = "Данный сорт в основном используется для приготовления рассыпчатого отварного картофеля, воздушного пюре и в качестве отличной начинки для пирогов. Растение имеет хорошую устойчивость к фитофторозу, ризоктониозу, парше, но показывает среднюю устойчивость к вирусам.",
            imageUri = null,
            imageUrl = "https://agrostory.com/upload/medialibrary/ca2/ca27c295a95dd9953014fd3963b051f5.jpg",
            variety = PotatoVariety.Table,
            ripening = PeriodRipening.MediumLate,
            productivity = Productivity.High,
        ),
        Potato(
            id = 3,
            name = "Синеглазка",
            description = "Клубни крупные, овальной и слегка приплюснутой формы. Имеют массу до 200 грамм. Кожура розоватой окраски с ярко выраженным сине-фиолетовым оттенком. Глазки насыщенного темно-синего цвета, откуда и произошло название сорта. На срезе мякоть белая. Содержание крахмала невысокое, но зато данный сорт имеет высокую урожайность и отменный вкус. Демонстрирует хорошую устойчивость к различным заболеваниям. При этом растение растет мощное, с крепкими стеблями и имеет хорошо развитую корневую систему и обильную зеленую массу. Первый урожай можно снимать уже в июне. Приготовленная картошка рассыпчатая, поэтому идеально подходит для пюре и выпекания. Имеет нежный приятный вкус.",
            imageUri = null,
            imageUrl = "https://agrostory.com/upload/medialibrary/8b3/8b356fda33548e48e05d935bd07b1aec.jpg",
            variety = PotatoVariety.Table,
            ripening = PeriodRipening.MediumRipe,
            productivity = Productivity.High,
        ),
        Potato(
            id = 4,
            name = "Киви",
            description = "По внешнему виду клубни данного картофеля очень напоминает киви. Является генетически модифицированным продуктом. Плоды крупные, немного вытянутой формы, хорошо хранятся. По праву считается одним из самых урожайных сортов картофеля. Неприхотливое растение, но любит обильное солнце и увлажненную почву. Хорошо отзывается на удобрения. При этом «Киви» хорошо противостоит различным грибковым и бактериальным инфекциям и практически не подвергается нападению колорадских жуков и прочих насекомых - вредителей. К недостаткам сорта можно отнести довольно длительный процесс готовки картофеля (более 40 минут) и необычный вкус, который устраивает далеко не всех. Тем не менее, благодаря рассыпчатой структуре мякоти клубни идеально подходят для приготовления пюре.",
            imageUri = null,
            imageUrl = "https://agrostory.com/upload/medialibrary/c83/c83354b5ad34c77677341b270e5c6afb.jpg",
            variety = PotatoVariety.Table,
            ripening = PeriodRipening.Late,
            productivity = Productivity.High,
        ),
        Potato(
            id = 5,
            name = "Беллароза",
            description = "Был выведен германскими селекционерами и быстро приобрел популярность. Плоды имеют овальную форму с красноватой кожурой и массу до 200 грамм. Растение неприхотливо, отлично переносит неблагоприятные погодные условия, в том числе засуху. Демонстрирует устойчивость ко многим возбудителям болезней. Вкусовые качества клубней соответствуют наивысшему стандарту, при этом после термической обработки картофель сохраняет рассыпчатость. Наиболее подходящие сорта для приготовления жареного картофеля.",
            imageUri = null,
            imageUrl = "https://agrostory.com/upload/medialibrary/fb8/fb8be322b8771d05ba924b9e4e61f23b.jpg",
            variety = PotatoVariety.Table,
            ripening = PeriodRipening.MediumRipe,
            productivity = Productivity.High,
        ),
        Potato(
            id = 6,
            name = "Конкорд",
            description = "Клубни имеют удлиненную форму и массу до 120 грамм. Цвет кожуры желтый. Мякоть имеет светлый оттенок. Подходит для жарки идеально. Достаточно неприхотлив и обладает превосходной устойчивость к заболеваниям (парше, нематоде, раку и прочим болезням), но часто заражается фитофторозом.",
            imageUri = null,
            imageUrl = "https://agrostory.com/upload/medialibrary/357/357eb76c3500a49637e8782a79c595d0.jpg",
            variety = PotatoVariety.Universal,
            ripening = PeriodRipening.MediumRipe,
            productivity = Productivity.High,
        ),
        Potato(
            id = 7,
            name = "Розара",
            description = "Выведен голландскими селекционерами. Клубни имеют овальную и слегка вытянутую форму с розовато-красной окраской кожуры. Мякоть светлая, желтоватого оттенка. Клубни не развариваются, не рассыпаются и имеют потрясающий вкус. Растение неприхотливо и устойчиво к нематоде, фитофторозу, парше и прочим заболеваниям.",
            imageUri = null,
            imageUrl = "https://agrostory.com/upload/medialibrary/262/26218e6603073257d0353f4eb1a6f719.jpg",
            variety = PotatoVariety.Universal,
            ripening = PeriodRipening.MediumRipe,
            productivity = Productivity.High,
        ),
        Potato(
            id = 8,
            name = "Пикассо",
            description = "Клубни имеют округлую форму и желтый цвет кожуры с розоватыми мелкими глазками. Мякоть светло-кремовая. Товарная масса до 125 грамм. Картофель с великолепными вкусовыми характеристиками. Хорошо подходит для жарки. Картофель хорошо хранится. Сорт демонстрирует хорошую устойчивость к картофельной нематоде, но восприимчив к фитофторозу.",
            imageUri = null,
            imageUrl = "https://agrostory.com/upload/medialibrary/489/489efd933fa1ec33c4f06db3362078d3.jpg",
            variety = PotatoVariety.Table,
            ripening = PeriodRipening.Late,
            productivity = Productivity.High,
        ),
        Potato(
            id = 9,
            name = "Зарница",
            description = "Имеет красноватые овальные клубни с мелкими глазками. Мякоть светло-желтого оттенка. Идеально подходит для жарки, производства чипсов и картофеля фри. Обладает хорошим нежным вкусом, слабо разваривается.  Сорт демонстрирует хорошую устойчивость к засухе. Пригоден для выращивания на всех типах почв. Имеет среднюю устойчивость к таким заболеваниям, как черная ножка, парша обыкновенная, ризоктониоз и фитофтороз.",
            imageUri = null,
            imageUrl = "https://agrostory.com/upload/medialibrary/c6b/c6b9eed755c60bb603a4bd77d5df0fa1.jpg",
            variety = PotatoVariety.Table,
            ripening = PeriodRipening.Late,
            productivity = Productivity.High,
        ),
        Potato(
            id = 10,
            name = "Аргос",
            description = "У клубней слегка удлиненная форма, желтый цвет кожуры и светло-желтая мякоть.  Хорошо подходит для жарки, выпечки, приготовления супов. Хорошо переносит жаркий климат. Устойчив к картофельной нематоде и раку. Умеренно восприимчив к фитофторозу, часто поражается паршой.",
            imageUri = null,
            imageUrl = "https://agrostory.com/upload/medialibrary/437/437df727d6a547cd99bb91fb8dd93aff.jpg",
            variety = PotatoVariety.Table,
            ripening = PeriodRipening.MediumLate,
            productivity = Productivity.High,
        ),
    )
}