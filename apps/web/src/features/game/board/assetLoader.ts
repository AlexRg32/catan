import terrainClay from '../../../assets/board/terrain-clay.svg'
import terrainDesert from '../../../assets/board/terrain-desert.svg'
import terrainGrain from '../../../assets/board/terrain-grain.svg'
import terrainOre from '../../../assets/board/terrain-ore.svg'
import terrainWood from '../../../assets/board/terrain-wood.svg'
import terrainWool from '../../../assets/board/terrain-wool.svg'
import harborClay from '../../../assets/board/harbor-clay.svg'
import harborGeneric from '../../../assets/board/harbor-generic.svg'
import harborGrain from '../../../assets/board/harbor-grain.svg'
import harborOre from '../../../assets/board/harbor-ore.svg'
import harborWood from '../../../assets/board/harbor-wood.svg'
import harborWool from '../../../assets/board/harbor-wool.svg'

export type AssetAtlas = {
  terrain: Record<string, string>
  harbor: Record<string, string>
}

const atlas: AssetAtlas = {
  terrain: {
    WOOD: terrainWood,
    WOOL: terrainWool,
    GRAIN: terrainGrain,
    CLAY: terrainClay,
    ORE: terrainOre,
    DESERT: terrainDesert,
  },
  harbor: {
    GENERIC: harborGeneric,
    WOOD: harborWood,
    WOOL: harborWool,
    GRAIN: harborGrain,
    CLAY: harborClay,
    ORE: harborOre,
  },
}

export function loadAssetAtlas(): AssetAtlas {
  return atlas
}
